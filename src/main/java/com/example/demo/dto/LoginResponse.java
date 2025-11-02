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
    private java.util.Set<String> roles;
    private boolean success;
}
