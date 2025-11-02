package com.example.demo.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Set<String> roles; // Puede ser null o vacío, siempre se asigna USER por defecto
}