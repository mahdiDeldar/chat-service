package com.chubock.chatservice.repository;

import com.chubock.chatservice.entity.Chat;
import com.chubock.chatservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepository extends AbstractRepository<Message> {

    Page<Message> findByChatOrderByCreateDateDesc(Chat chat, Pageable pageable);

}
