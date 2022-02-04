package com.chubock.chatservice.repository;

import com.chubock.chatservice.entity.Chat;
import com.chubock.chatservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends AbstractRepository<Message> {

    Page<Message> findByChatOrderByCreateDateDesc(Chat chat, Pageable pageable);

    @Query("select m from Message m where m.chat.id = :chatId")
    List<Message> findAllMessageByChat(@Param("chatId") String chatId);

}
