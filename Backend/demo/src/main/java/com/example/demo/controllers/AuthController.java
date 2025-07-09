package com.example.demo.controllers;

import org.springframework.security.core.Authentication;
import com.example.demo.dtos.request.LoginRequest;
import com.example.demo.dtos.request.RegisterRequest;
import com.example.demo.dtos.response.AuthResponse;
import com.example.demo.services.UserService;  // ✔️ Usa UserService

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dtos.request.UpdateUserRequest; // ✔️ Importa UpdateUserRequest

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;  // ✔️ Inyecta UserService

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);  // ✔️ Delega a UserService
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String email = authentication.getName(); // Usuario logueado
        userService.deleteUserByEmail(email);
        return ResponseEntity.ok("Cuenta eliminada correctamente.");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        
        String currentEmail = authentication.getName();
        userService.updateUser(currentEmail, request);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }


}