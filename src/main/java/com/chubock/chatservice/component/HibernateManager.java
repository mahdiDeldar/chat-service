package com.chubock.chatservice.component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class HibernateManager {

    public void afterCommit(Runnable runnable) {

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            public void afterCommit() {
                runnable.run();
            }
        });
    }

}
