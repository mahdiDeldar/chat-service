package com.chubock.chatservice.service;

import com.chubock.chatservice.conf.IntegrationTest;
import com.chubock.chatservice.entity.User;
import com.chubock.chatservice.model.ChatModel;
import com.chubock.chatservice.model.MessageModel;
import com.chubock.chatservice.model.UserModel;
import com.chubock.chatservice.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@IntegrationTest
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    @Test
    public void testSendMessageWithoutSubject() {

        String senderUsername = RandomStringUtils.randomAlphanumeric(6);
        String receiverUsername = RandomStringUtils.randomAlphanumeric(6);
        String messageBody = RandomStringUtils.randomAlphanumeric(100);

        testSendingMessage(senderUsername, receiverUsername, messageBody, null);
    }

    @Test
    public void testSendMessageWithSubject() {

        String senderUsername = RandomStringUtils.randomAlphanumeric(6);
        String receiverUsername = RandomStringUtils.randomAlphanumeric(6);
        String messageBody = RandomStringUtils.randomAlphanumeric(100);
        String subject = RandomStringUtils.randomAlphanumeric(20);

        testSendingMessage(senderUsername, receiverUsername, messageBody, subject);

    }

    private void testSendingMessage(String senderUsername, String receiverUsername, String messageBody, String subject) {

        User sender = User.builder()
                .username(senderUsername)
                .build();

        userRepository.save(sender);

        User receiver = User.builder()
                .username(receiverUsername)
                .build();

        userRepository.save(receiver);

        MessageModel messageModel = MessageModel.builder()
                .sender(UserModel.builder().username(senderUsername).build())
                .receiver(UserModel.builder().username(receiverUsername).build())
                .body(messageBody)
                .subject(subject)
                .build();

        messageService.send(messageModel);

        Page<ChatModel> senderChats = chatService.getChats(senderUsername, PageRequest.of(0, 1));

        Assert.assertFalse(senderChats.getContent().isEmpty());

        ChatModel chatModel = senderChats.getContent().get(0);

        Assert.assertEquals(chatModel.getUser().getUsername(), receiverUsername);

        if (subject == null)
            Assert.assertNull(chatModel.getSubject());
        else
            Assert.assertEquals(chatModel.getSubject(), subject);

        Page<MessageModel> senderChatMessages = messageService.getUserChatMessages(senderUsername, chatModel.getId(), PageRequest.of(0, 1));

        Assert.assertFalse(senderChatMessages.isEmpty());

        messageModel = senderChatMessages.getContent().get(0);

        Assert.assertEquals(messageModel.getSender().getUsername(), senderUsername);
        Assert.assertEquals(messageModel.getReceiver().getUsername(), receiverUsername);
        Assert.assertEquals(messageModel.getBody(), messageBody);

        Page<ChatModel> receiverChats = chatService.getChats(receiverUsername, PageRequest.of(0, 1));

        Assert.assertFalse(receiverChats.getContent().isEmpty());

        chatModel = receiverChats.getContent().get(0);

        Assert.assertEquals(chatModel.getUser().getUsername(), senderUsername);

        if (subject == null)
            Assert.assertNull(chatModel.getSubject());
        else
            Assert.assertEquals(chatModel.getSubject(), subject);

        Page<MessageModel> receiverChatMessages = messageService.getUserChatMessages(receiverUsername, chatModel.getId(), PageRequest.of(0, 1));

        Assert.assertFalse(receiverChatMessages.isEmpty());

        messageModel = receiverChatMessages.getContent().get(0);

        Assert.assertEquals(messageModel.getSender().getUsername(), senderUsername);
        Assert.assertEquals(messageModel.getReceiver().getUsername(), receiverUsername);
        Assert.assertEquals(messageModel.getBody(), messageBody);
    }


}
