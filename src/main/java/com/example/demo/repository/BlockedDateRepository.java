package com.example.demo.repository;

import com.example.demo.model.BlockedDate;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlockedDateRepository extends JpaRepository<BlockedDate, Long> {
        List<BlockedDate> findByPropertyId(Long propertyId);
        
        List<BlockedDate> findByPropertyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long propertyId, LocalDate endDate, LocalDate startDate
    );
}
