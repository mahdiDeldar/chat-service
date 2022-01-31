package com.chubock.chatservice.service;

import com.chubock.chatservice.entity.MqttConnectionDetail;
import com.chubock.chatservice.entity.User;
import com.chubock.chatservice.model.MqttConnectionDetailModel;
import com.chubock.chatservice.repository.MQTTConnectionDetailRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MqttConnectionDetailService {

    private final String mqttHost;
    private final String mqttUsersTopicPrefix;
    private final String mqttUsersInboxTopicSuffix;
    private final String mqttUsersOutboxTopicSuffix;

    private final MQTTConnectionDetailRepository mqttConnectionDetailRepository;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public MqttConnectionDetailService(@Value("${app.chat.mqtt.host}") String mqttHost,
                                       @Value("${app.chat.mqtt.users-topic-prefix}") String mqttUsersTopicPrefix,
                                       @Value("${app.chat.mqtt.users-inbox-topic-suffix}") String mqttUsersInboxTopicSuffix,
                                       @Value("${app.chat.mqtt.users-outbox-topic-suffix}") String mqttUsersOutboxTopicSuffix,
                                       MQTTConnectionDetailRepository mqttConnectionDetailRepository,
                                       UserService userService) {
        this.mqttHost = mqttHost;
        this.mqttUsersTopicPrefix = mqttUsersTopicPrefix;
        this.mqttUsersInboxTopicSuffix = mqttUsersInboxTopicSuffix;
        this.mqttUsersOutboxTopicSuffix = mqttUsersOutboxTopicSuffix;
        this.mqttConnectionDetailRepository = mqttConnectionDetailRepository;
        this.userService = userService;

        //because of EMQ setting we initialize the password encoder here.
        passwordEncoder = new BCryptPasswordEncoder();

    }

    @Transactional
    public MqttConnectionDetailModel refresh(String username) {

        User user = userService.get(username);

        final String password = RandomStringUtils.randomAlphanumeric(16);

        MqttConnectionDetail detail = mqttConnectionDetailRepository.findByUser(user)
                .orElseGet(() -> {

                    MqttConnectionDetail mqttConnectionDetail = MqttConnectionDetail.builder()
                            .user(user)
                            .password(passwordEncoder.encode(password))
                            .build();

                    return mqttConnectionDetailRepository.save(mqttConnectionDetail);

                });

        detail.setPassword(passwordEncoder.encode(password));

        mqttConnectionDetailRepository.save(detail);

        return MqttConnectionDetailModel.builder()
                .host(mqttHost)
                .clientId(username + "_" + System.currentTimeMillis())
                .username(username)
                .password(password)
                .inboxTopic(mqttUsersTopicPrefix + username + mqttUsersInboxTopicSuffix)
                .outboxTopic(mqttUsersTopicPrefix + username + mqttUsersOutboxTopicSuffix)
                .build();


    }
}
