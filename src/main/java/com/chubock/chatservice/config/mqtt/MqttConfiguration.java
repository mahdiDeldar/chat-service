package com.chubock.chatservice.config.mqtt;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Log4j2
@Component
@Configuration
@ConfigurationProperties(MqttConfiguration.PREFIX)
public class MqttConfiguration {

    public static final String PREFIX = "spring.stream.mqtt";

    private String host;
    private String clientIdPrefix;
    private String username;
    private String password;
    private String topic;
    private int timeout;
    private int keepalive;

}
