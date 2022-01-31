package com.chubock.chatservice.model;

import com.chubock.chatservice.entity.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageModel extends AbstractModel<Message> {

    private UserModel sender;

    @NotNull(message = "receiver can't be null")
    @Valid
    private UserModel receiver;

    private String subject;

    @NotEmpty(message = "body can't be empty")
    private String body;

    private ChatModel chat;

    @Override
    public void fill(Message entity) {
        super.fill(entity);
        setBody(entity.getBody());
        setSender(new UserModel(entity.getSender().getId()));
        setReceiver(new UserModel(entity.getReceiver().getId()));
    }
}
