package com.example.demo.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long propertyId;
    private String status; 
}
