package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private Long userId;
    private String email;
    private String name;
    private String role;
    private boolean success;
}
