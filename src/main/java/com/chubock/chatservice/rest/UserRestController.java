package com.chubock.chatservice.rest;

import com.chubock.chatservice.model.UserModel;
import com.chubock.chatservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/profile")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Getting profile")
    public UserModel getProfile(Authentication authentication) {
        return userService.getProfile(authentication.getName());
    }

    @PutMapping
    @Operation(summary = "Updating profile")
    public UserModel updateProfile(Authentication authentication, @RequestBody UserModel model) {
        model.setId(authentication.getName());
        return userService.updateProfile(model);
    }

}
