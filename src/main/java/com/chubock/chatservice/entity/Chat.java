package com.chubock.chatservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CHATS")
@NoArgsConstructor
public class Chat extends AbstractEntity {

    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    private User firstUser;
    @ManyToOne(fetch = FetchType.LAZY)
    private User secondUser;

    @OneToOne
    private Message lastMessage;

    private int firstUserUnseenMessagesCount;
    private int secondUserUnseenMessagesCount;

    @Column(columnDefinition = "timestamp")
    private LocalDateTime lastMessageDate;

    @Column(columnDefinition = "timestamp")
    private LocalDateTime firstUserLastSeenDate;

    @Column(columnDefinition = "timestamp")
    private LocalDateTime secondUserLastSeenDate;

    public void incrementFirstUserUnSeenMessagesCount() {
        firstUserUnseenMessagesCount++;
    }

    public void incrementSecondUserUnSeenMessagesCount() {
        secondUserUnseenMessagesCount++;
    }

}
