package com.chubock.chatservice.component.firebase;

import com.chubock.chatservice.model.NotificationModel;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseMessagingPublisher {

    private final FirebaseMessagingHolder firebaseMessagingHolder;

    public FirebaseMessagingPublisher(FirebaseMessagingHolder firebaseMessagingHolder) {
        this.firebaseMessagingHolder = firebaseMessagingHolder;
    }

    public String sendNotification(NotificationModel model)
            throws FirebaseMessagingException {

        if (firebaseMessagingHolder.getFirebaseMessaging() == null)
            throw new RuntimeException("firebase messaging is not provided");

        log.info("sending notification {}: {} to {}", model.getTitle(), model.getBody(), model.getToken());

        Notification notification = Notification
                .builder()
                .setTitle(model.getTitle())
                .setBody(model.getBody())
                .build();

        Message message = Message
                .builder()
                .setToken(model.getToken())
                .setNotification(notification)
                .putAllData(model.getData())
                .build();

        return firebaseMessagingHolder.getFirebaseMessaging().send(message);
    }

}
