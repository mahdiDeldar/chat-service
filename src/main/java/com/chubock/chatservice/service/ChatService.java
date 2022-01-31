package com.chubock.chatservice.service;

import com.chubock.chatservice.entity.Chat;
import com.chubock.chatservice.entity.User;
import com.chubock.chatservice.model.*;
import com.chubock.chatservice.repository.ChatRepository;
import com.chubock.chatservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @Transactional(readOnly = true)
    public Page<ChatModel> getChats(String username, Pageable pageable) {
        return getChats(username, null, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ChatModel> getChats(String username, ChatSearchModel chatSearchModel, Pageable pageable) {

        return userRepository.findById(username)
                .map(user -> getChats(user, chatSearchModel, pageable)
                        .map(chat -> getUserChatModel(user, chat)))
                .orElseGet(() -> Page.empty(pageable));

    }

    @Transactional(readOnly = true)
    public ChatStatisticsModel getChatStatistics(String userId) {
        return ChatStatisticsModel.builder()
                .unseenChatsCount(chatRepository.countFirstUserUnseenChats(userId)
                        + chatRepository.countSecondUserUnseenChats(userId))
                .build();
    }

    private Page<Chat> getChats(User user, ChatSearchModel chatSearchModel, Pageable pageable) {

        if (chatSearchModel == null)
            return chatRepository.findByMember(user, pageable);

        if (chatSearchModel.getReceiverId() == null) {

            if (chatSearchModel.getSubject() != null)
                return chatRepository.findByMemberAndSubject(user, chatSearchModel.getSubject(), pageable);
            else if (chatSearchModel.getWithSubject() == null)
                return chatRepository.findByMember(user, pageable);
            else if (chatSearchModel.getWithSubject())
                return chatRepository.findByMemberWithSubject(user, pageable);
            else
                return chatRepository.findByMemberWithoutSubject(user, pageable);

        } else {

            User receiver = userService.get(chatSearchModel.getReceiverId());

            if (chatSearchModel.getSubject() != null)
                return chatRepository.findByMembersAndSubject(user, receiver, chatSearchModel.getSubject(), pageable);
            else if (chatSearchModel.getWithSubject() == null)
                return chatRepository.findByMembers(user, receiver, pageable);
            else if (chatSearchModel.getWithSubject())
                return chatRepository.findByMembersWithSubject(user, receiver, pageable);
            else
                return chatRepository.findByMembersWithoutSubject(user, receiver, pageable);

        }

    }

    private ChatModel getUserChatModel(User user, Chat chat) {

        ChatModel model = ModelFactory.of(chat, ChatModel.class);

        model.setLastMessage(ModelFactory.of(chat.getLastMessage(), MessageModel.class));

        if (chat.getFirstUser().equals(user)) {
            model.setUser(UserModel.of(chat.getSecondUser()));
            model.setLastSeenDate(chat.getSecondUserLastSeenDate());
            model.setUnseenMessagesCount(chat.getFirstUserUnseenMessagesCount());
        } else {
            model.setUser(UserModel.of(chat.getFirstUser()));
            model.setLastSeenDate(chat.getFirstUserLastSeenDate());
            model.setUnseenMessagesCount(chat.getSecondUserUnseenMessagesCount());
        }

        return model;

    }
}
