package com.example.demo.repositories;

import com.example.demo.models.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    // Listar alojamientos destacados y activos
    List<Accommodation> findByIsFeaturedTrueAndActiveTrue();

    // Buscar alojamientos con filtros y que estén activos
    @Query("SELECT a FROM Accommodation a WHERE " +
           "a.active = true AND " +
           "(:location IS NULL OR a.location LIKE %:location%) AND " +
           "(:type IS NULL OR a.type = :type) AND " +
           "(:minPrice IS NULL OR a.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.price <= :maxPrice) AND " +
           "(:guests IS NULL OR a.maxGuests >= :guests)")
    List<Accommodation> searchWithFilters(String location, String type, 
                                         Double minPrice, Double maxPrice, 
                                         Integer guests);

    // (Opcional) Listar todos los alojamientos activos
    List<Accommodation> findByActiveTrue();

    @Query("SELECT a FROM Accommodation a WHERE a.rating >= :minRating AND a.reviews >= :minReviews AND a.isFeatured = true ORDER BY a.rating DESC")
       List<Accommodation> findFeaturedByRating(double minRating, int minReviews);


}
