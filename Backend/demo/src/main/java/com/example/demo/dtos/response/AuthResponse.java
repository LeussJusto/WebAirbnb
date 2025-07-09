package com.example.demo.dtos.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;  // JWT
    private String email;
    private String name;
}