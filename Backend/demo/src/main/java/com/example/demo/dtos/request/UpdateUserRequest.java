package com.example.demo.dtos.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private String password;
    private boolean isHost;
}
