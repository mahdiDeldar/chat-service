package com.chubock.chatservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class NotificationModel {

    public static final String PROP_MESSAGE_ID = "messageId";
    public static final String PROP_CHAT_ID = "chatId";
    public static final String PROP_SUBJECT = "subject";

    private String token;
    private String title;
    private String body;
    @Builder.Default
    private Map<String, String> data = new HashMap<>();
}
