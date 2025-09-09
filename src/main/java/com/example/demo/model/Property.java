package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000) // Para descripciones largas
    private String description;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private double pricePerNight;
    
    @ManyToOne(fetch = FetchType.LAZY) // LAZY por defecto, pero es buena práctica especificarlo
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
}