package com.chubock.chatservice.service;

import com.chubock.chatservice.endpoint.UserEndpoint;
import com.chubock.chatservice.entity.Chat;
import com.chubock.chatservice.entity.Message;
import com.chubock.chatservice.entity.MqttConnectionDetail;
import com.chubock.chatservice.entity.User;
import com.chubock.chatservice.model.UserModel;
import com.chubock.chatservice.repository.ChatRepository;
import com.chubock.chatservice.repository.MQTTConnectionDetailRepository;
import com.chubock.chatservice.repository.MessageRepository;
import com.chubock.chatservice.repository.UserRepository;
import com.ctc.wstx.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserEndpoint userEndpoint;

    private final ChatRepository chatRepository;

    private final MessageRepository messageRepository;

    private final MQTTConnectionDetailRepository mqttConnectionDetailRepository;

    public UserService(UserRepository userRepository, UserEndpoint userEndpoint,
                       ChatRepository chatRepository, MessageRepository messageRepository,
                       MQTTConnectionDetailRepository mqttConnectionDetailRepository) {
        this.userRepository = userRepository;
        this.userEndpoint = userEndpoint;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.mqttConnectionDetailRepository = mqttConnectionDetailRepository;
    }

    @Transactional
    public UserModel getProfile(String id) {
        return UserModel.of(get(id));
    }

    @Transactional
    public UserModel updateProfile(UserModel model) {
        User user = get(model.getId());

        user.setFullName(model.getFullName());
        user.setFirebaseToken(model.getFirebaseToken());
        user.setImageUrl(model.getImageUrl());
        return UserModel.of(user);
    }

    User get(String id) {
        return userRepository.findById(id)
                .orElseGet(() -> {
                    UserModel userDetails = userEndpoint.getUserDetails(id);
                    User user = User.builder()
                            .id(userDetails.getId())
                            .username(userDetails.getUsername())
                            .fullName(userDetails.getFullName())
                            .imageUrl(userDetails.getImageUrl())
                            .build();
                    return userRepository.save(user);
                });
    }

    @Transactional
    public ResponseEntity<Boolean> deleteUser(String id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
        }
        List<Chat> chatList = chatRepository.findByFirstUserOrSecondUser(user.get(), user.get());
        if (!chatList.isEmpty()) {
            chatList.forEach(chat -> {
                chat.setLastMessage(null);
                List<Message> messageList = messageRepository.findAllMessageByChat(chat.getId());
                if (!messageList.isEmpty()) {
                    messageList.forEach(message -> messageRepository.delete(message));
                }
                chatRepository.delete(chat);
            });
        }
        Optional<MqttConnectionDetail> mqtdetail = mqttConnectionDetailRepository.findByUser(user.get());
        if (mqtdetail.isPresent()) {
            mqttConnectionDetailRepository.delete(mqtdetail.get());
        }
        userRepository.delete(user.get());
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }
}