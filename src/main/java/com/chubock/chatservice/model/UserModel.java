package com.chubock.chatservice.model;

import com.chubock.chatservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @NotEmpty
    private String id;
    private String username;
    private String fullName;
    private String imageUrl;

    private String firebaseToken;

    public UserModel(String id) {
        setId(id);
    }

    public void fill(User entity) {
        setId(entity.getId());
        setUsername(entity.getUsername());
        setFullName(entity.getFullName());
        setImageUrl(entity.getImageUrl());
        setFirebaseToken(entity.getFirebaseToken());
    }

    public static UserModel of (User user) {
        UserModel model = new UserModel();
        model.fill(user);
        return model;
    }
}
