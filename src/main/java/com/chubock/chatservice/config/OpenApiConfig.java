package com.chubock.chatservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private final String appUrl;

    public OpenApiConfig(@Value("${app.url}") String appUrl) {
        this.appUrl = appUrl;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(appUrl);
        return new OpenAPI().servers(List.of(server));
    }

}
