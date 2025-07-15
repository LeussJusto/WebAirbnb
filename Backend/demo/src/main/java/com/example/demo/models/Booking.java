package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkIn;
    private LocalDate checkOut;

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // PROXIMA, PASADA, CANCELADA

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // El que reserva

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation; // Lo que reserva
    
    @ManyToOne
    @JoinColumn(name = "trip_plan_id")
    private TripPlan tripPlan;

}

