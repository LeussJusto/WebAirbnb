package com.example.demo.service;

import com.example.demo.model.Property;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final PropertyRepository propertyRepository;

	public Review createReview(Long propertyId, int rating, String comment) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));

		Review review = Review.builder()
				.rating(rating)
				.comment(comment)
				.property(property)
				.user(user)
				.build();

		return reviewRepository.save(review);
	}

	@Transactional(readOnly = true)
	public List<Review> getReviewsForProperty(Long propertyId) {
		Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));
		return reviewRepository.findAll().stream().filter(r -> r.getProperty().getId().equals(property.getId())).toList();
	}

	public void deleteReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		if (!review.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Not authorized to delete this review");
		}

		reviewRepository.delete(review);
	}
}
