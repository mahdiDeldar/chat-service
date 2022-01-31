package com.chubock.chatservice.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public String toJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }

    @SneakyThrows
    public <T> T fromJson(String body, Class<T> clazz) {
        return objectMapper.readValue(body, clazz);
    }

}
