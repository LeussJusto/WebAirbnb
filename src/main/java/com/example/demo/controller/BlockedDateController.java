package com.example.demo.controller;

import com.example.demo.model.BlockedDate;
import com.example.demo.service.BlockedDateService;
import com.example.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/blocked-dates")
@RequiredArgsConstructor
public class BlockedDateController {

    private final BlockedDateService blockedDateService;

    @PostMapping
    public ResponseEntity<BlockedDate> add(
            @RequestParam Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @AuthenticationPrincipal UserDetailsImpl currentUser) { // ✅ Agregar este parámetro
        
        return ResponseEntity.ok(blockedDateService.addBlockedDate(propertyId, start, end, currentUser));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<BlockedDate>> listForProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(blockedDateService.getBlockedDatesForProperty(propertyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        
        blockedDateService.deleteBlockedDate(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}