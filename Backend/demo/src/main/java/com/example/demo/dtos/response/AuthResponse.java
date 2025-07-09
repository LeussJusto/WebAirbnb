package com.example.demo.dtos.response;

import com.example.demo.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User user;  // ✅ Enviar el objeto completo
}
