package com.example.demo.dtos.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private boolean isHost;
}