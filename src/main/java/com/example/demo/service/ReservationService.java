package com.example.demo.service;

import com.example.demo.dto.ReservationDTO;
import com.example.demo.dto.ReservationResponseDTO;
import com.example.demo.security.UserDetailsImpl;

import java.util.List;

public interface ReservationService {
    ReservationResponseDTO createReservation(ReservationDTO reservationDTO, UserDetailsImpl  currentUser);
    List<ReservationResponseDTO> getAllReservations();
    ReservationResponseDTO getReservationById(Long id);
    ReservationResponseDTO updateStatus(Long id, String status);
    void deleteReservation(Long id);
}
