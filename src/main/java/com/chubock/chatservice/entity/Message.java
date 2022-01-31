package com.chubock.chatservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MESSAGES")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Message extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    private String body;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Chat chat;


}




