package com.example.demo.repositories;

import com.example.demo.models.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findByIsFeaturedTrue();
    
    @Query("SELECT a FROM Accommodation a WHERE " +
           "(:location IS NULL OR a.location LIKE %:location%) AND " +
           "(:type IS NULL OR a.type = :type) AND " +
           "(:minPrice IS NULL OR a.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.price <= :maxPrice) AND " +
           "(:guests IS NULL OR a.maxGuests >= :guests)")
    List<Accommodation> searchWithFilters(String location, String type, 
                                         Double minPrice, Double maxPrice, 
                                         Integer guests);
}