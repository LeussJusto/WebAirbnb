package com.example.demo.dtos.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingResponse {
    private Long id;
    private String accommodationTitle;
    private String location;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status;
    private Long tripPlanId;
    private String hostName;
    private String hostEmail;
    private String hostPhone;

}
