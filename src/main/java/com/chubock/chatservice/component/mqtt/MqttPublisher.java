package com.chubock.chatservice.component.mqtt;

import com.chubock.chatservice.component.JsonConverter;
import com.chubock.chatservice.model.MessageModel;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MqttPublisher {

    private final MqttManager mqttManager;
    private final JsonConverter jsonConverter;

    private final String usersTopicPrefix;
    private final String usersInboxTopicSuffix;

    public MqttPublisher(MqttManager mqttManager, JsonConverter jsonConverter,
                         @Value("${app.chat.mqtt.users-topic-prefix}") String usersTopicPrefix,
                         @Value("${app.chat.mqtt.users-inbox-topic-suffix}") String usersInboxTopicSuffix) {
        this.mqttManager = mqttManager;
        this.jsonConverter = jsonConverter;

        this.usersTopicPrefix = usersTopicPrefix;
        this.usersInboxTopicSuffix = usersInboxTopicSuffix;
    }

    public void publish(MessageModel message) {
        String topic = usersTopicPrefix + message.getReceiver().getId() + usersInboxTopicSuffix;
        publish(topic, jsonConverter.toJson(message));
    }

    public void publish(String topic,String pushMessage){
        publish(topic, pushMessage, 0, false);
    }

    public void publish(String topicName,String messageBody, int qos,boolean retained) {

        log.info("publishing message {} to topic {}", messageBody, topicName);

        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(messageBody.getBytes());

        MqttTopic topic = mqttManager.getClient().getTopic(topicName);

        if(topic == null)
            throw new IllegalArgumentException("topic " + topicName + " not found");

        try {
            topic.publish(message);
        } catch (MqttException e) {
            log.error("error in publishing message to topic {}", topicName, e);
        }
    }
}
