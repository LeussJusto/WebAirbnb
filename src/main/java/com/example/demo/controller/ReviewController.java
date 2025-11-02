package com.example.demo.controller;

import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<Review> create(@RequestParam Long propertyId, @RequestParam int rating, @RequestParam String comment) {
		Review r = reviewService.createReview(propertyId, rating, comment);
		return ResponseEntity.ok(r);
	}

	@GetMapping("/property/{propertyId}")
	public ResponseEntity<List<Review>> listForProperty(@PathVariable Long propertyId) {
		return ResponseEntity.ok(reviewService.getReviewsForProperty(propertyId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		reviewService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}
}
