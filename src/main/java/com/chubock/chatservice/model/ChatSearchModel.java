package com.chubock.chatservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSearchModel {

    private String senderId;
    private String receiverId;
    private String subject;
    private Boolean withSubject;

}
