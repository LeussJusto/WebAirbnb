package com.example.demo.service;

import com.example.demo.model.BlockedDate;
import com.example.demo.model.Property;
import com.example.demo.repository.BlockedDateRepository;
import com.example.demo.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.security.UserDetailsImpl;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockedDateService {

    private final BlockedDateRepository blockedDateRepository;
    private final PropertyRepository propertyRepository;

    public BlockedDate addBlockedDate(Long propertyId, LocalDate start, LocalDate end, 
                                     UserDetailsImpl currentUser) {
        if (start == null || end == null) {
            throw new RuntimeException("Start date and end date are required");
        }
        
        if (start.isAfter(end)) {
            throw new RuntimeException("Start date must be before or equal to end date");
        }
        
        if (start.isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot block dates in the past");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only property owner can block dates");
        }

        List<BlockedDate> conflicts = blockedDateRepository
            .findByPropertyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                propertyId, end, start
            );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Dates conflict with existing blocked dates: " + 
                                     conflicts.get(0).getStartDate() + " to " + 
                                     conflicts.get(0).getEndDate());
        }

        BlockedDate bd = BlockedDate.builder()
                .property(property)
                .startDate(start)
                .endDate(end)
                .build();
                
        return blockedDateRepository.save(bd);
    }

    @Transactional(readOnly = true)
    public List<BlockedDate> getBlockedDatesForProperty(Long propertyId) {
        return blockedDateRepository.findByPropertyId(propertyId);
    }

    public void deleteBlockedDate(Long id, UserDetailsImpl currentUser) {
        BlockedDate blockedDate = blockedDateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blocked date not found"));

        if (!blockedDate.getProperty().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only property owner can delete blocked dates");
        }

        blockedDateRepository.delete(blockedDate);
    }
}