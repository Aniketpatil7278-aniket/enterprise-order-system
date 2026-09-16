package com.enterprise.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users" ,
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")}
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    //before insert (lifecycle)
    @PrePersist
    public void onCreate(){
        LocalDateTime now = LocalDateTime.now();

        createdAt=now;
        updatedAt= now;
    }

    //before update
    @PreUpdate
    public void onUpdate(){
        updatedAt=LocalDateTime.now();
    }


}
