package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.Role;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;

        @PostMapping("/register")
        public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        return ResponseEntity.badRequest().body(
                                        RegisterResponse.builder()
                                                        .message("El email ya está registrado")
                                                        .success(false)
                                                        .build()
                        );
                }

                Set<Role> roles = new HashSet<>();
                roles.add(Role.USER); // Siempre asignar USER
                if (request.getRoles() != null) {
                        for (String r : request.getRoles()) {
                                try {
                                        Role role = Role.valueOf(r.toUpperCase());
                                        roles.add(role);
                                } catch (IllegalArgumentException ignored) {}
                        }
                }

                User user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .roles(roles)
                                .build();

                userRepository.save(user);

                UserDetailsImpl userDetails = UserDetailsImpl.build(user);
                String token = jwtUtils.generateJwtToken(userDetails);

                RegisterResponse response = RegisterResponse.builder()
                                .message("Usuario registrado correctamente")
                                .userId(user.getId())
                                .email(user.getEmail())
                                .name(user.getName())
                                .roles(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()))
                                .token(token)
                                .success(true)
                                .build();

                return ResponseEntity.ok(response);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                String token = jwtUtils.generateJwtToken(userDetails);

                LoginResponse response = new LoginResponse(
                                "Login exitoso",
                                token,
                                userDetails.getId(),
                                userDetails.getEmail(),
                                userDetails.getName(),
                                userDetails.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                                true
                );

                return ResponseEntity.ok(response);
        }

}