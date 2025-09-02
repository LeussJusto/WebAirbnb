package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod; // ej: "CARD", "PAYPAL"
    private double amount;
    private boolean completed;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
