package com.chubock.chatservice.rest;

import com.chubock.chatservice.exception.ChatNotFoundException;
import com.chubock.chatservice.model.ChatModel;
import com.chubock.chatservice.model.ChatSearchModel;
import com.chubock.chatservice.model.ChatStatisticsModel;
import com.chubock.chatservice.model.MessageModel;
import com.chubock.chatservice.service.ChatService;
import com.chubock.chatservice.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatRestService {

    private final ChatService chatService;
    private final MessageService messageService;

    public ChatRestService(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @GetMapping
    @Operation(summary = "Get current user chats")
    public Page<ChatModel> getChats(@RequestParam(value = "receiverId", required = false) String receiverId,
                                    @RequestParam(value = "subject", required = false) String subject,
                                    @RequestParam(value = "withSubject", required = false) Boolean withSubject,
                                    Authentication authentication,
                                    Pageable pageable) {

        ChatSearchModel chatSearchModel = ChatSearchModel.builder()
                .receiverId(receiverId)
                .subject(subject)
                .withSubject(withSubject)
                .build();

        return chatService.getChats(authentication.getName(), chatSearchModel, pageable);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Get chat messages")
    public Page<MessageModel> getChatMessages(@PathVariable("id") String id, Pageable pageable, Authentication authentication) {
        try {
            return messageService.getUserChatMessages(authentication.getName(), id, pageable);
        } catch (ChatNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.chat.notFound");
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get current user chat statistics")
    public ChatStatisticsModel getChatStatistics(Authentication authentication) {
        return chatService.getChatStatistics(authentication.getName());
    }

}
