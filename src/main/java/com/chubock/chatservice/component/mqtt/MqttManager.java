package com.chubock.chatservice.component.mqtt;

import com.chubock.chatservice.config.mqtt.MqttConfiguration;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
public class MqttManager implements MqttCallback {

    private MqttClient client;

    private final MqttConfiguration mqttConfiguration;

    private final String usersTopicPrefix;
    private final String usersOutboxTopicSuffix;

    private MqttSubscriber mqttSubscriber;

    public MqttManager(MqttConfiguration mqttConfiguration,
                       @Value("${app.chat.mqtt.users-topic-prefix}") String usersTopicPrefix,
                       @Value("${app.chat.mqtt.users-outbox-topic-suffix}") String usersOutboxTopicSuffix) {

        this.mqttConfiguration = mqttConfiguration;
        this.usersTopicPrefix = usersTopicPrefix;
        this.usersOutboxTopicSuffix = usersOutboxTopicSuffix;

    }

    @PostConstruct
    private void init() {
        CompletableFuture.runAsync(this::connect);
    }

    public MqttConnectOptions mqttConnectOptions() {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(mqttConfiguration.getUsername());
        if (mqttConfiguration.getPassword() != null)
            options.setPassword(mqttConfiguration.getPassword().toCharArray());
        options.setConnectionTimeout(mqttConfiguration.getTimeout());
        options.setKeepAliveInterval(mqttConfiguration.getKeepalive());

        return options;
    }

    @SneakyThrows
    private void connect() {

        do {
            try {
                log.info("trying to connect to emq at {}", mqttConfiguration.getHost());
                client = new MqttClient(mqttConfiguration.getHost(),
                        mqttConfiguration.getClientIdPrefix() + System.currentTimeMillis(), new MemoryPersistence());
                client.connect(mqttConnectOptions());
                client.setCallback(this);
                log.info("connected to mqtt");
                client.subscribe(usersTopicPrefix + "+" + usersOutboxTopicSuffix, mqttSubscriber);
                log.info("subscribed on {}", usersTopicPrefix + "+" + usersOutboxTopicSuffix);
            } catch (Exception e) {
                log.warn("couldn't connect to emq at {}. retrying later...", mqttConfiguration.getHost(), e);
                Thread.sleep(5000);
            }

        } while (!client.isConnected());

    }

    @Override
    @SneakyThrows
    public void connectionLost(Throwable cause) {
        // After the connection is lost, it is usually reconnected here.
        log.warn("Connection disconnected");
        connect();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // The message obtained after subscribe will be executed here.
        log.info("Receive message subject: " + topic);
        log.info("Receive message Qos: " + message.getQos());
        log.info("Receive message content: " + new String(message.getPayload()));
    }

    public MqttClient getClient() {
        return client;
    }

    @Autowired
    public void setMqttSubscriber(MqttSubscriber mqttSubscriber) {
        this.mqttSubscriber = mqttSubscriber;
    }
}
