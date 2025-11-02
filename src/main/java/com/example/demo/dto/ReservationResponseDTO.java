package com.example.demo.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String status;
    private PropertyDTO property;
    private UserDTO guest;
}
