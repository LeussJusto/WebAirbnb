package com.example.demo.controllers;

import com.example.demo.dtos.request.AccommodationRequest;
import com.example.demo.dtos.response.AccommodationCardDto;
import com.example.demo.dtos.response.AccommodationDetailDto;
import com.example.demo.services.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;

    @GetMapping("/featured")
    public ResponseEntity<List<AccommodationCardDto>> getFeaturedAccommodations() {
        return ResponseEntity.ok(accommodationService.getFeaturedAccommodations());
    }

    @PutMapping("/{id}/featured")
    public ResponseEntity<Void> setFeatured(
            @PathVariable Long id,
            @RequestParam boolean featured,
            Authentication authentication) {
        String hostEmail = authentication.getName();
        // Si quieres, verifica que el host sea el mismo o tenga permisos antes
        accommodationService.setFeatured(id, featured);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    public ResponseEntity<List<AccommodationCardDto>> searchAccommodations(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer guests) {
        return ResponseEntity.ok(accommodationService.searchAccommodations(
                location, type, minPrice, maxPrice, guests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDetailDto> getAccommodationDetails(@PathVariable Long id) {
        return ResponseEntity.ok(accommodationService.getAccommodationDetails(id));
    }

    @PostMapping
    public ResponseEntity<AccommodationDetailDto> createAccommodation(
            @Valid @RequestBody AccommodationRequest request,
            Authentication authentication) {   // Aquí recibes el usuario autenticado
        String hostEmail = authentication.getName();  // Obtienes el email del usuario logueado
        return ResponseEntity.ok(accommodationService.createAccommodation(request, hostEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDetailDto> updateAccommodation(
            @PathVariable Long id,
            @Valid @RequestBody AccommodationRequest request,
            Authentication authentication) {

        String hostEmail = authentication.getName();
        return ResponseEntity.ok(accommodationService.updateAccommodation(id, request, hostEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(
            @PathVariable Long id,
            Authentication authentication) {

        String hostEmail = authentication.getName();
        accommodationService.deleteAccommodation(id, hostEmail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateAccommodation(
            @PathVariable Long id,
            Authentication authentication) {

        String hostEmail = authentication.getName();
        accommodationService.activateAccommodation(id, hostEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/featured/rating")
    public ResponseEntity<List<AccommodationCardDto>> getFeaturedByRating(
            @RequestParam double minRating,
            @RequestParam int minReviews) {
        List<AccommodationCardDto> featured = accommodationService.getFeaturedByRating(minRating, minReviews);
        return ResponseEntity.ok(featured);
    }


    @PostMapping("/{id}/review")
    public ResponseEntity<Void> addReview(@PathVariable Long id, @RequestParam double rating) {
        accommodationService.addReview(id, rating);
        return ResponseEntity.ok().build();
    }

}