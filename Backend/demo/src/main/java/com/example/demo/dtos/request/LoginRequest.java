package com.example.demo.dtos.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}