package com.enterprise.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
    private Long id;

    private String name;

    private String email;

//    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
