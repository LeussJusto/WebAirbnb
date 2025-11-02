package com.example.demo.service;

import com.example.demo.dto.PaymentResponseDTO;
import com.example.demo.model.Payment;
import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationStatus;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentResponseDTO simulatePayment(Long reservationId, String paymentMethod, 
                                             UserDetailsImpl currentUser) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getGuest().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only pay for your own reservations");
        }

        if (reservation.getPayment() != null) {
            throw new RuntimeException("Reservation already has a payment");
        }

        if (!paymentMethod.matches("CARD|PAYPAL|CASH")) {
            throw new RuntimeException("Invalid payment method. Use: CARD, PAYPAL, or CASH");
        }

        Payment payment = Payment.builder()
                .paymentMethod(paymentMethod)
                .amount(reservation.getTotalPrice())
                .completed(true)
                .reservation(reservation)
                .build();

        Payment saved = paymentRepository.save(payment);

        reservation.setPayment(saved);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPayment(Long id, UserDetailsImpl currentUser) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getReservation().getGuest().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view your own payments");
        }

        return mapToDTO(payment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .completed(payment.getCompleted())
                .reservationId(payment.getReservation().getId())
                .build();
    }
}