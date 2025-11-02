package com.example.demo.controller;

import com.example.demo.dto.ReservationDTO;
import com.example.demo.dto.ReservationResponseDTO; 
import com.example.demo.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.demo.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationResponseDTO createReservation(
            @RequestBody ReservationDTO reservationDTO,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return reservationService.createReservation(reservationDTO, currentUser);
    }

    @GetMapping
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ReservationResponseDTO getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @PutMapping("/{id}/status")
    public ReservationResponseDTO updateStatus(@PathVariable Long id, @RequestParam String status) {
        return reservationService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }
}
