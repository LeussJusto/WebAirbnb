package com.example.demo.dtos.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long accommodationId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Long tripPlanId;

}
