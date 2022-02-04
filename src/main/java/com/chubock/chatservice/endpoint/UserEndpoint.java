package com.chubock.chatservice.endpoint;

import com.chubock.chatservice.model.UserModel;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userEndpoint", url = "http://user-service/users")
public interface UserEndpoint {

    @GetMapping("me")
    @Headers("Content-Type: application/json")
    UserModel getUserDetails();

    @GetMapping("{id}")
    @Headers("Content-Type: application/json")
    UserModel getUserDetails(@PathVariable("id") String id);
}
