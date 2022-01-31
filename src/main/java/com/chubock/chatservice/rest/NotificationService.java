package com.chubock.chatservice.rest;

import com.chubock.chatservice.model.NotificationModel;
import com.chubock.chatservice.component.firebase.FirebaseMessagingPublisher;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationService {

    private final FirebaseMessagingPublisher firebaseMessagingPublisher;

    public NotificationService(FirebaseMessagingPublisher firebaseMessagingPublisher) {
        this.firebaseMessagingPublisher = firebaseMessagingPublisher;
    }

    @PostMapping
    public String sendNotification(@RequestBody NotificationModel model) throws FirebaseMessagingException {
        return firebaseMessagingPublisher.sendNotification(model);
    }

}
