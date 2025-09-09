package com.example.demo.service;

import com.example.demo.dto.PropertyDTO;
import com.example.demo.model.Property;
import java.util.List;

public interface PropertyService {
    Property createProperty(PropertyDTO propertyDTO);
    Property updateProperty(Long id, PropertyDTO propertyDTO);
    void deleteProperty(Long id);
    Property getPropertyById(Long id);
    Property getPropertyWithDetails(Long id);
    List<Property> getAllProperties();
    List<Property> getPropertiesByOwnerId(Long ownerId);
    List<Property> getMyProperties(); 
}