package com.example.demo.service;

import com.example.demo.dto.PropertyDTO;
import com.example.demo.model.Property;
import com.example.demo.model.User;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.RoleEnum;
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
        // Obtener el usuario autenticado desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Email del usuario logueado
        
        System.out.println("Creating property for authenticated user: " + email);
        
        // Buscar el usuario autenticado
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
        System.out.println("Owner found: " + owner.getEmail() + " with role: " + owner.getRole());
        
        // Validar que el usuario tenga rol HOST
        if (owner.getRole() != RoleEnum.HOST) {
            throw new RuntimeException("Only users with HOST role can create properties. Current role: " + owner.getRole());
        }
        
        // Crear la propiedad
        Property property = Property.builder()
                .title(propertyDTO.getTitle())
                .description(propertyDTO.getDescription())
                .address(propertyDTO.getAddress())
                .pricePerNight(propertyDTO.getPricePerNight())
                .owner(owner) // Automáticamente el usuario autenticado
                .build();
        
        System.out.println("Property created successfully for owner: " + owner.getEmail());
        return propertyRepository.save(property);
    }

    @Override
    public Property updateProperty(Long id, PropertyDTO propertyDTO) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
        // Buscar la propiedad
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        
        // Verificar que el usuario sea el propietario de la propiedad
        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own properties");
        }
        
        // Actualizar campos
        property.setTitle(propertyDTO.getTitle());
        property.setDescription(propertyDTO.getDescription());
        property.setAddress(propertyDTO.getAddress());
        property.setPricePerNight(propertyDTO.getPricePerNight());
        
        return propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(Long id) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
        // Buscar la propiedad
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        
        // Verificar que el usuario sea el propietario de la propiedad
        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own properties");
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
        return propertyRepository.findByOwnerId(ownerId);
    }
    
    // Nuevo método: obtener las propiedades del usuario autenticado
    @Override
    @Transactional(readOnly = true)
    public List<Property> getMyProperties() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        
        return propertyRepository.findByOwnerId(currentUser.getId());
    }
}