package com.chubock.chatservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqttConnectionDetailModel {

    private String host;
    private String clientId;
    private String username;
    private String password;
    private String inboxTopic;
    private String outboxTopic;

}
