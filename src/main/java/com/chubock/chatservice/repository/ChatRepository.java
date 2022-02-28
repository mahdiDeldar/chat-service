package com.chubock.chatservice.repository;

import com.chubock.chatservice.entity.Chat;
import com.chubock.chatservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends AbstractRepository<Chat> {

    @Query("select c from Chat c join fetch c.firstUser join fetch c.secondUser where c.id = :id")
    Optional<Chat> findByIdWithUsers(@Param("id") String id);

    @EntityGraph(attributePaths = "lastMessage")
    @Query("select c from Chat c where c.firstUser = :user or c.secondUser = :user")
    Page<Chat> findByMember(@Param("user") User user, Pageable pageable);

    @EntityGraph(attributePaths = "lastMessage")
    @Query("select c from Chat c where c.subject = :subject and (c.firstUser = :user or c.secondUser = :user)")
    Page<Chat> findByMemberAndSubject(@Param("user") User user, @Param("subject") String subject, Pageable pageable);

    @EntityGraph(attributePaths = "lastMessage")
    @Query("select c from Chat c where (c.firstUser = :user1 and c.secondUser = :user2) or (c.firstUser = :user2 and c.secondUser = :user1)")
    Page<Chat> findByMembers(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    @EntityGraph(attributePaths = "lastMessage")
    @Query("select c from Chat c where c.subject = :subject and " +
            "((c.firstUser = :user1 and c.secondUser = :user2) or (c.firstUser = :user2 and c.secondUser = :user1))")
    Page<Chat> findByMembersAndSubject(@Param("user1") User user1, @Param("user2") User user2, @Param("subject") String subject, Pageable pageable);

    @EntityGraph(attributePaths = "lastMessage")
    @Query("select c from Chat c where c.subject is not null and (c.firstUser = :user or c.secondUser = :user)")
    Page<Chat> findByMemberWithSubject(@Param("user") User user, Pageable pageable);

    @EntityGraph(attributePaths = "lastMessage")
    @Query("select c from Chat c where c.subject is not null and " +
            "((c.firstUser = :user1 and c.secondUser = :user2) or (c.firstUser = :user2 and c.secondUser = :user1))")
    Page<Chat> findByMembersWithSubject(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    @Query("select c from Chat c where c.subject is null and (c.firstUser = :user or c.secondUser = :user)")
    @EntityGraph(attributePaths = "lastMessage")
    Page<Chat> findByMemberWithoutSubject(@Param("user") User user, Pageable pageable);

    @Query("select c from Chat c where c.subject is null and " +
            "((c.firstUser = :user1 and c.secondUser = :user2) or (c.firstUser = :user2 and c.secondUser = :user1))")
    @EntityGraph(attributePaths = "lastMessage")
    Page<Chat> findByMembersWithoutSubject(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    @Query("select c from Chat c where c.subject is null and ((c.firstUser.id = :sender and c.secondUser.id = :receiver) or (c.firstUser.id = :receiver and c.secondUser.id = :sender))")
    Optional<Chat> getChat(@Param("sender") String sender, @Param("receiver") String receiver);

    @Query("select c from Chat c where c.subject = :subject and ((c.firstUser.id = :sender and c.secondUser.id = :receiver) or (c.firstUser.id = :receiver and c.secondUser.id = :sender))")
    Optional<Chat> getChat(@Param("sender") String sender, @Param("receiver") String receiver, @Param("subject") String subject);

    @Query("select count(c) from Chat c where c.firstUser.id = :userId and c.firstUserUnseenMessagesCount > 0")
    long countFirstUserUnseenChats(String userId);

    @Query("select count(c) from Chat c where c.secondUser.id = :userId and c.secondUserUnseenMessagesCount > 0")
    long countSecondUserUnseenChats(String userId);

    @Query("select c from Chat c where c.firstUser.id = :userid or c.secondUser.id = :userid")
    List<Chat> findChatsFirstUser(@Param("userid") String userid);

    List<Chat> findByFirstUserOrSecondUser(User firstUser,User secondUser);
}
