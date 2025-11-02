package com.example.demo.controller;

import com.example.demo.model.Favorite;
import com.example.demo.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<Favorite> add(@RequestParam Long propertyId) {
        return ResponseEntity.ok(favoriteService.addFavorite(propertyId));
    }

    @DeleteMapping
    public ResponseEntity<?> remove(@RequestParam Long propertyId) {
        favoriteService.removeFavorite(propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> listForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesForUser(userId));
    }

    @GetMapping("/is-favorite")
    public ResponseEntity<Boolean> isFavorite(@RequestParam Long propertyId) {
        return ResponseEntity.ok(favoriteService.isFavorite(propertyId));
    }
}
