package com.chubock.chatservice.repository;

import com.chubock.chatservice.entity.MqttConnectionDetail;
import com.chubock.chatservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MQTTConnectionDetailRepository extends JpaRepository<MqttConnectionDetail, Long> {

    Optional<MqttConnectionDetail> findByUser(User user);

}
