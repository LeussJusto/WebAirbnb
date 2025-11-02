package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000) 
    private String description;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private double pricePerNight;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
}