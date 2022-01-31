package com.chubock.chatservice.component.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;


@Slf4j
@Configuration
public class FirebaseMessagingHolder {

    private final String firebaseApp;
    private final String firebaseCredentialsPath;

    private final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingHolder(@Value("${firebase.app}") String firebaseApp, @Value("${firebase.credentials}") String firebaseCredentialsPath) {
        this.firebaseApp = firebaseApp;
        this.firebaseCredentialsPath = firebaseCredentialsPath;

        firebaseMessaging = createFirebaseMessaging();

    }

    public FirebaseMessaging getFirebaseMessaging() {
        return firebaseMessaging;
    }

    private FirebaseMessaging createFirebaseMessaging() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new FileInputStream(firebaseCredentialsPath));
            FirebaseOptions firebaseOptions = FirebaseOptions
                    .builder()
                    .setCredentials(googleCredentials)
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, firebaseApp);

            return FirebaseMessaging.getInstance(app);
        } catch (Exception e) {
            log.error("error in creating firebaseMessaging bean", e);
            return null;
        }
    }

}
