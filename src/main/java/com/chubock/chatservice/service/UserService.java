package com.chubock.chatservice.service;

import com.chubock.chatservice.endpoint.UserEndpoint;
import com.chubock.chatservice.entity.User;
import com.chubock.chatservice.model.UserModel;
import com.chubock.chatservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserEndpoint userEndpoint;


    public UserService(UserRepository userRepository, UserEndpoint userEndpoint) {
        this.userRepository = userRepository;
        this.userEndpoint = userEndpoint;
    }

    @Transactional
    public UserModel getProfile(String id) {
        return UserModel.of(get(id));
    }

    @Transactional
    public UserModel updateProfile(UserModel model) {
        User user = get(model.getId());

        user.setFullName(model.getFullName());
        user.setFirebaseToken(model.getFirebaseToken());
        user.setImageUrl(model.getImageUrl());

        return UserModel.of(user);

    }

    User get(String id) {

        return userRepository.findById(id)
                .orElseGet(() -> {

                    UserModel userDetails = userEndpoint.getUserDetails(id);

                    User user = User.builder()
                            .id(userDetails.getId())
                            .username(userDetails.getUsername())
                            .fullName(userDetails.getFullName())
                            .imageUrl(userDetails.getImageUrl())
                            .build();

                    return userRepository.save(user);

                });
    }
}
