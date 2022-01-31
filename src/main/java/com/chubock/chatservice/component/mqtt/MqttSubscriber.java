package com.chubock.chatservice.component.mqtt;

import com.chubock.chatservice.component.JsonConverter;
import com.chubock.chatservice.model.MessageModel;
import com.chubock.chatservice.model.UserModel;
import com.chubock.chatservice.service.MessageService;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MqttSubscriber implements IMqttMessageListener {

    private final JsonConverter jsonConverter;
    private final MessageService messageService;

    public MqttSubscriber(JsonConverter jsonConverter, MessageService messageService) {
        this.jsonConverter = jsonConverter;
        this.messageService = messageService;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        log.info("received message {} from topic {}", new String(message.getPayload()), topic);

        try {

            String sender = getUsername(topic);
            MessageModel messageModel = jsonConverter.fromJson(new String(message.getPayload()), MessageModel.class);
            messageModel.setSender(new UserModel(sender));

            messageService.send(messageModel);
        } catch (Exception e) {
            log.error("error in processing message {}", new String(message.getPayload()), e);
        }

    }

    private String getUsername(String topic) {
        String temp = topic.substring(0, topic.lastIndexOf("/"));
        return temp.substring(temp.lastIndexOf("/") + 1);
    }
}
