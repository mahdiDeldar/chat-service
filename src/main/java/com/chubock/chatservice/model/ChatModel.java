package com.chubock.chatservice.model;

import com.chubock.chatservice.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ChatModel extends AbstractModel<Chat> {

    private String subject;

    private UserModel user;

    private MessageModel lastMessage;
    private LocalDateTime lastMessageDate;

    private Integer unseenMessagesCount;
    private LocalDateTime lastSeenDate;

    public ChatModel(String id) {
        super(id);
    }

    @Override
    public void fill(Chat entity) {
        super.fill(entity);
        setSubject(entity.getSubject());
        setLastMessageDate(entity.getLastMessageDate());
    }

}

