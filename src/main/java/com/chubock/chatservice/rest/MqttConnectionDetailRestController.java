package com.chubock.chatservice.rest;

import com.chubock.chatservice.model.MqttConnectionDetailModel;
import com.chubock.chatservice.service.MqttConnectionDetailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mqtt")
public class MqttConnectionDetailRestController {

    private final MqttConnectionDetailService mqttConnectionDetailService;

    public MqttConnectionDetailRestController(MqttConnectionDetailService mqttConnectionDetailService) {
        this.mqttConnectionDetailService = mqttConnectionDetailService;
    }

    @PostMapping
    @Operation(summary = "Refresh and get mqtt connection details")
    public MqttConnectionDetailModel refresh(Authentication authentication) {
        return mqttConnectionDetailService.refresh(authentication.getName());
    }

}
