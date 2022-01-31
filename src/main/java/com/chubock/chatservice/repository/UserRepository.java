package com.chubock.chatservice.repository;

import com.chubock.chatservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
