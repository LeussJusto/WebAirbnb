package com.example.demo.service;

import com.example.demo.model.Favorite;
import com.example.demo.model.Property;
import com.example.demo.model.User;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final PropertyRepository propertyRepository;

	public Favorite addFavorite(Long propertyId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));

		Optional<Favorite> existing = favoriteRepository.findAll().stream()
				.filter(f -> f.getUser().getId().equals(user.getId()) && f.getProperty().getId().equals(property.getId()))
				.findFirst();
		if (existing.isPresent()) return existing.get();

		Favorite favorite = Favorite.builder().user(user).property(property).build();
		return favoriteRepository.save(favorite);
	}

	public void removeFavorite(Long propertyId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Property not found"));

		favoriteRepository.findAll().stream()
				.filter(f -> f.getUser().getId().equals(user.getId()) && f.getProperty().getId().equals(property.getId()))
				.findFirst()
				.ifPresent(favoriteRepository::delete);
	}

	public boolean isFavorite(Long propertyId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		return favoriteRepository.findAll().stream()
				.anyMatch(f -> f.getUser().getId().equals(user.getId()) && f.getProperty().getId().equals(propertyId));
	}

	@Transactional(readOnly = true)
	public List<Favorite> getFavoritesForUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		return favoriteRepository.findAll().stream().filter(f -> f.getUser().getId().equals(user.getId())).toList();
	}
}
