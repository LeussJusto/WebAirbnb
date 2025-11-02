package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Long id;
    private String paymentMethod;
    private Double amount;
    private Boolean completed;
    private Long reservationId;
}