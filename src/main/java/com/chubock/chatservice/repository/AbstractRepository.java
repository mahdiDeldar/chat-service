package com.chubock.chatservice.repository;

import com.chubock.chatservice.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbstractRepository<T extends AbstractEntity> extends JpaRepository<T, String> {
}
