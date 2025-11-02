package com.example.demo.service;

import com.example.demo.dto.PropertyDTO;
import com.example.demo.model.Property;
import com.example.demo.model.User;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyServiceImpl implements PropertyService {
    
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Override
    public Property createProperty(PropertyDTO propertyDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); 
        
        System.out.println("Creating property for authenticated user: " + email);
        
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
    System.out.println("Owner found: " + owner.getEmail() + " with roles: " + owner.getRoles());
        
        if (owner.getRoles() == null || !owner.getRoles().contains(Role.HOST)) {
            throw new RuntimeException("Only users with HOST role can create properties. Current roles: " + owner.getRoles());
        }
        
        Property property = Property.builder()
                .title(propertyDTO.getTitle())
                .description(propertyDTO.getDescription())
                .address(propertyDTO.getAddress())
                .pricePerNight(propertyDTO.getPricePerNight())
                .owner(owner) 
                .build();
        
        System.out.println("Property created successfully for owner: " + owner.getEmail());
        return propertyRepository.save(property);
    }

    @Override
    public Property updateProperty(Long id, PropertyDTO propertyDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        
    System.out.println("[PropertyService] updateProperty - currentUser.id=" + currentUser.getId() + ", roles=" + currentUser.getRoles());
    System.out.println("[PropertyService] updateProperty - property.id=" + property.getId() + ", owner.id=" + (property.getOwner() != null ? property.getOwner().getId() : "null")
        + ", owner.roles=" + (property.getOwner() != null ? property.getOwner().getRoles() : "null"));

    if (property.getOwner() == null || !property.getOwner().getId().equals(currentUser.getId())) {
        throw new RuntimeException("You can only update your own properties. propertyOwnerId=" + (property.getOwner()!=null?property.getOwner().getId():"null") + ", currentUserId=" + currentUser.getId());
    }
        
        property.setTitle(propertyDTO.getTitle());
        property.setDescription(propertyDTO.getDescription());
        property.setAddress(propertyDTO.getAddress());
        property.setPricePerNight(propertyDTO.getPricePerNight());
        
        return propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        
        System.out.println("[PropertyService] deleteProperty - currentUser.id=" + currentUser.getId() + ", roles=" + currentUser.getRoles());
        System.out.println("[PropertyService] deleteProperty - property.id=" + property.getId() + ", owner.id=" + (property.getOwner() != null ? property.getOwner().getId() : "null")
                + ", owner.roles=" + (property.getOwner() != null ? property.getOwner().getRoles() : "null"));

        if (property.getOwner() == null || !property.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own properties. propertyOwnerId=" + (property.getOwner()!=null?property.getOwner().getId():"null") + ", currentUserId=" + currentUser.getId());
        }
        
        propertyRepository.delete(property);
    }

    @Override
    @Transactional(readOnly = true)
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Property getPropertyWithDetails(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        
        return property;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> getPropertiesByOwnerId(Long ownerId) {
        return propertyRepository.findByOwnerIdOrderByIdDesc(ownerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Property> getMyProperties() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
        return propertyRepository.findByOwnerIdOrderByIdDesc(currentUser.getId());
    }
}