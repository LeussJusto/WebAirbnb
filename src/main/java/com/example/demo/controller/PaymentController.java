package com.example.demo.controller;

import com.example.demo.dto.PaymentResponseDTO;
import com.example.demo.service.PaymentService;
import com.example.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments") 
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> pay(
            @RequestParam Long reservationId,
            @RequestParam String method,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        
        return ResponseEntity.ok(paymentService.simulatePayment(
            reservationId, method, currentUser
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> get(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        
        return ResponseEntity.ok(paymentService.getPayment(id, currentUser));
    }
}