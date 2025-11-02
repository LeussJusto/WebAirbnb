package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PropertyRepository propertyRepository;
private final UserRepository userRepository;

        @Override
        public ReservationResponseDTO createReservation(ReservationDTO reservationDTO, UserDetailsImpl currentUserDetails) {
        if (reservationDTO.getStartDate() == null || reservationDTO.getEndDate() == null) {
                throw new RuntimeException("Start date and end date are required");
        }
        
        if (reservationDTO.getPropertyId() == null) {
                throw new RuntimeException("Property ID is required");
        }
        
        if (reservationDTO.getStartDate().isAfter(reservationDTO.getEndDate())) {
                throw new RuntimeException("Start date must be before end date");
        }

        Property property = propertyRepository.findById(reservationDTO.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User guest = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        long days = ChronoUnit.DAYS.between(reservationDTO.getStartDate(), reservationDTO.getEndDate());
        if (days <= 0) {
                throw new RuntimeException("Reservation must be at least 1 day");
        }
        
        double totalPrice = days * property.getPricePerNight();

        ReservationStatus status = ReservationStatus.PENDING;
        if (reservationDTO.getStatus() != null && !reservationDTO.getStatus().isEmpty()) {
                try {
                status = ReservationStatus.valueOf(reservationDTO.getStatus().toUpperCase());
                } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + reservationDTO.getStatus() + 
                        ". Valid values are: PENDING, CONFIRMED, CANCELLED");
                }
        }

        Reservation reservation = Reservation.builder()
                .startDate(reservationDTO.getStartDate())
                .endDate(reservationDTO.getEndDate())
                .status(status) 
                .totalPrice(totalPrice)
                .property(property)
                .guest(guest)
                .build();

        return mapToResponseDTO(reservationRepository.save(reservation));
        }

    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponseDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return mapToResponseDTO(reservation);
    }

    @Override
    public ReservationResponseDTO updateStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(ReservationStatus.valueOf(status));
        return mapToResponseDTO(reservationRepository.save(reservation));
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    private ReservationResponseDTO mapToResponseDTO(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .property(PropertyDTO.builder()
                        .title(reservation.getProperty().getTitle())
                        .description(reservation.getProperty().getDescription())
                        .address(reservation.getProperty().getAddress())
                        .pricePerNight(reservation.getProperty().getPricePerNight())
                        .build())
                .guest(UserDTO.builder()
                        .id(reservation.getGuest().getId())
                        .name(reservation.getGuest().getName())
                        .email(reservation.getGuest().getEmail())
                        .roles(reservation.getGuest().getRoles())
                        .build())
                .build();
    }
}
