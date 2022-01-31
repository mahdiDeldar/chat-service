package com.chubock.chatservice.rest;

import com.chubock.chatservice.exception.UserNotFoundException;
import com.chubock.chatservice.model.MessageModel;
import com.chubock.chatservice.model.UserModel;
import com.chubock.chatservice.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageRestService {

    private final MessageService messageService;

    public MessageRestService(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(summary = "Send a message to a user")
    public MessageModel save(Authentication authentication, @Validated @RequestBody MessageModel messageModel) {
        try {
            messageModel.setSender(new UserModel(authentication.getName()));
            return messageService.send(messageModel);
        } catch (UserNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error.user.notFound");
        }
    }

}
