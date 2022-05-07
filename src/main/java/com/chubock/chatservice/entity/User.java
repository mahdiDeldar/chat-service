package com.chubock.chatservice.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SuperBuilder(toBuilder = true)
public class User {

    @Id
    private String id;
    private String username;
    private String fullName;
    @Builder.Default
    private String imageUrl = "";

    private String firebaseToken;

    @Builder.Default
    @CreatedDate
    private LocalDateTime createDate = LocalDateTime.now();
    @Builder.Default
    @LastModifiedDate
    private LocalDateTime lastModifiedDate = LocalDateTime.now();
    @Version
    private Integer version;

}
