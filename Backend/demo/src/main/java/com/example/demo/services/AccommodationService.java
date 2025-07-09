package com.example.demo.services;

import com.example.demo.dtos.request.AccommodationRequest;
import com.example.demo.dtos.response.UserProfileDto;
import com.example.demo.dtos.response.AccommodationCardDto;
import com.example.demo.dtos.response.AccommodationDetailDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Accommodation;
import com.example.demo.models.User;
import com.example.demo.repositories.AccommodationRepository;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<AccommodationCardDto> getFeaturedAccommodations() {
        // Solo alojamientos destacados y activos
        return accommodationRepository.findByIsFeaturedTrueAndActiveTrue()
                .stream()
                .map(this::convertToCardDto)
                .collect(Collectors.toList());
    }

    

    public List<AccommodationCardDto> searchAccommodations(String location, String type, 
                                                          Double minPrice, Double maxPrice, 
                                                          Integer guests) {
        // Solo alojamientos activos
        return accommodationRepository.searchWithFilters(location, type, minPrice, maxPrice, guests)
                .stream()
                .filter(Accommodation::isActive)
                .map(this::convertToCardDto)
                .collect(Collectors.toList());
    }

    public AccommodationDetailDto getAccommodationDetails(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .filter(Accommodation::isActive) // Solo activo
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found or inactive"));
        return convertToDetailDto(accommodation);
    }

    @Transactional
    public AccommodationDetailDto createAccommodation(AccommodationRequest request, String hostEmail) {
        User host = userRepository.findByEmail(hostEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Accommodation accommodation = modelMapper.map(request, Accommodation.class);
        accommodation.setHost(host);
        accommodation.setRating(5.0);
        accommodation.setReviews(0);
        accommodation.setActive(true); // activo por defecto

        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        return convertToDetailDto(savedAccommodation);
    }

    public AccommodationDetailDto updateAccommodation(Long id, AccommodationRequest request, String hostEmail) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        if (!accommodation.getHost().getEmail().equals(hostEmail)) {
            throw new RuntimeException("No autorizado para modificar este alojamiento");
        }

        accommodation.setTitle(request.getTitle());
        accommodation.setDescription(request.getDescription());
        accommodation.setPrice(request.getPrice());
        accommodation.setLocation(request.getLocation());
        accommodation.setImages(request.getImages());
        accommodation.setAmenities(request.getAmenities());
        accommodation.setMaxGuests(request.getMaxGuests());
        accommodation.setBedrooms(request.getBedrooms());
        accommodation.setBathrooms(request.getBathrooms());
        accommodation.setType(request.getType());

        accommodationRepository.save(accommodation);
        return modelMapper.map(accommodation, AccommodationDetailDto.class);
    }

    // Ahora solo desactivamos el alojamiento, no borramos físicamente
    public void deleteAccommodation(Long id, String hostEmail) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        if (!accommodation.getHost().getEmail().equals(hostEmail)) {
            throw new RuntimeException("No autorizado para eliminar este alojamiento");
        }

        accommodation.setActive(false);  // Desactivar en vez de borrar
        accommodationRepository.save(accommodation);
    }

    public void activateAccommodation(Long id, String hostEmail) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        if (!accommodation.getHost().getEmail().equals(hostEmail)) {
            throw new RuntimeException("No autorizado para activar este alojamiento");
        }

        accommodation.setActive(true); // Activar alojamiento
        accommodationRepository.save(accommodation);
    }

    private AccommodationCardDto convertToCardDto(Accommodation accommodation) {
        AccommodationCardDto dto = modelMapper.map(accommodation, AccommodationCardDto.class);
        dto.setMainImage(accommodation.getImages().isEmpty() ? "" : accommodation.getImages().get(0));
        return dto;
    }

    private AccommodationDetailDto convertToDetailDto(Accommodation accommodation) {
        AccommodationDetailDto dto = modelMapper.map(accommodation, AccommodationDetailDto.class);
        dto.setHost(modelMapper.map(accommodation.getHost(), UserProfileDto.class));
        return dto;
    }

    public List<AccommodationCardDto> getFeaturedByRating(double minRating, int minReviews) {
        List<Accommodation> featured = accommodationRepository.findFeaturedByRating(minRating, minReviews);
        return featured.stream()
                    .map(this::convertToCardDto)
                    .collect(Collectors.toList());
    }


    @Transactional
    public void addReview(Long accommodationId, double newRating) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found"));

        double currentRating = accommodation.getRating();
        int currentReviews = accommodation.getReviews();

        double updatedRating = ((currentRating * currentReviews) + newRating) / (currentReviews + 1);
        accommodation.setRating(updatedRating);
        accommodation.setReviews(currentReviews + 1);

        accommodationRepository.save(accommodation);

        // Actualiza si debe ser featured o no
        updateFeaturedStatus(accommodationId);
    }

    @Transactional
    public void setFeatured(Long accommodationId, boolean featured) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
            .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found"));
        accommodation.setFeatured(featured);
        accommodationRepository.save(accommodation);
    }
    
    public void updateFeaturedStatus(Long accommodationId) {
    Accommodation acc = accommodationRepository.findById(accommodationId)
        .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found"));
    if(acc.getRating() >= 4.0 && acc.getReviews() >= 10){
        acc.setFeatured(true);
    } else {
        acc.setFeatured(false);
    }
    accommodationRepository.save(acc);
}




}
