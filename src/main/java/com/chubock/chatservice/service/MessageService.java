package com.chubock.chatservice.service;

import com.chubock.chatservice.component.firebase.FirebaseMessagingPublisher;
import com.chubock.chatservice.component.HibernateManager;
import com.chubock.chatservice.component.mqtt.MqttPublisher;
import com.chubock.chatservice.entity.Chat;
import com.chubock.chatservice.entity.Message;
import com.chubock.chatservice.entity.User;
import com.chubock.chatservice.exception.ChatNotFoundException;
import com.chubock.chatservice.exception.UserNotFoundException;
import com.chubock.chatservice.model.*;
import com.chubock.chatservice.repository.ChatRepository;
import com.chubock.chatservice.repository.MessageRepository;
import com.chubock.chatservice.repository.UserRepository;
import com.jasongoodwin.monads.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    private final MqttPublisher mqttPublisher;
    private final FirebaseMessagingPublisher firebaseMessagingPublisher;
    private final HibernateManager hibernateManager;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository,
                          UserRepository userRepository, UserService userService,
                          MqttPublisher mqttPublisher, FirebaseMessagingPublisher firebaseMessagingPublisher, HibernateManager hibernateManager) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mqttPublisher = mqttPublisher;
        this.firebaseMessagingPublisher = firebaseMessagingPublisher;
        this.hibernateManager = hibernateManager;
    }

    @Transactional
    public Page<MessageModel> getUserChatMessages(String id, String chatId, Pageable pageable) {

        User user = userRepository.findById(id)
                .orElseThrow();

        Chat chat = chatRepository.findByIdWithUsers(chatId)
                .filter(c -> c.getFirstUser().equals(user) || c.getSecondUser().equals(user))
                .orElseThrow(ChatNotFoundException::new);

        if (chat.getFirstUser().equals(user)) {
            chat.setFirstUserUnseenMessagesCount(0);
            chat.setFirstUserLastSeenDate(LocalDateTime.now());
        } else {
            chat.setSecondUserUnseenMessagesCount(0);
            chat.setSecondUserLastSeenDate(LocalDateTime.now());
        }

        return messageRepository.findByChatOrderByCreateDateDesc(chat, pageable)
                .map(message -> ModelFactory.of(message, MessageModel.class));

    }

    @Transactional
    public MessageModel send(MessageModel messageModel) {

        if (messageModel.getReceiver().getId().equals(messageModel.getSender().getId()))
            throw new IllegalArgumentException("Not allowed to send message to yourself");

        Message message = save(messageModel);

        MessageModel ret = ModelFactory.of(message, MessageModel.class);
        ret.setReceiver(UserModel.of(message.getReceiver()));
        ret.setChat(new ChatModel(message.getChat().getId()));

        hibernateManager.afterCommit(() -> sendNotifications(ret));

        return ret;

    }

    private Message save(MessageModel messageModel) {

        User sender = Optional.ofNullable(messageModel.getSender())
                .map(UserModel::getId)
                .map(userService::get)
                .orElseThrow(UserNotFoundException::new);

        User receiver = Optional.ofNullable(messageModel.getReceiver())
                .map(UserModel::getId)
                .map(userService::get)
                .orElseThrow(UserNotFoundException::new);

        Chat chat = getChat(sender, receiver, messageModel.getSubject());

        Message message = Message.builder()
                .body(messageModel.getBody())
                .sender(sender)
                .receiver(receiver)
                .chat(chat)
                .build();

        chat.setLastMessage(message);
        chat.setLastMessageDate(LocalDateTime.now());

        if (chat.getFirstUser().equals(receiver)) {
            chat.incrementFirstUserUnSeenMessagesCount();
            chat.setSecondUserLastSeenDate(LocalDateTime.now());
        } else {
            chat.incrementSecondUserUnSeenMessagesCount();
            chat.setFirstUserLastSeenDate(LocalDateTime.now());
        }

        chatRepository.save(chat);

        return messageRepository.save(message);
    }

    private Chat getChat(User sender, User receiver, String subject) {

        if (subject == null)
            return chatRepository.getChat(sender.getId(), receiver.getId())
                    .orElseGet(() -> createChat(sender, receiver));
        else
            return chatRepository.getChat(sender.getId(), receiver.getId(), subject)
                    .orElseGet(() -> createChat(sender, receiver, subject));

    }

    private Chat createChat(User sender, User receiver) {
        return createChat(sender, receiver, null);
    }

    private Chat createChat(User sender, User receiver, String subject) {

        Chat build = Chat.builder()
                .firstUser(sender)
                .secondUser(receiver)
                .subject(subject)
                .build();

        return chatRepository.save(build);
    }

    private void sendNotifications(MessageModel message) {

        if (StringUtils.isNotBlank(message.getReceiver().getFirebaseToken())) {

            Try.ofFailable(() -> {

                NotificationModel notificationModel = NotificationModel.builder()
                        .token(message.getReceiver().getFirebaseToken())
                        .title(message.getSender().getFullName() + " sent you a message")
                        .body(message.getBody())
                        .build();

                notificationModel.getData().put(NotificationModel.PROP_MESSAGE_ID, message.getId());
                notificationModel.getData().put("chatId", message.getChat().getId());

                if (message.getChat().getSubject() != null)
                    notificationModel.getData().put("subject", message.getChat().getSubject());

                return firebaseMessagingPublisher.sendNotification(notificationModel);

            }).onFailure(e -> log.error("error in sending message {} notification to {}", message.getId(), e));

        }

        mqttPublisher.publish(message);

    }

}
