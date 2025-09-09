package com.example.demo.controller;

import com.example.demo.dto.PropertyDTO;
import com.example.demo.model.Property;
import com.example.demo.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {
    
    private final PropertyService propertyService;

    // ✅ CREAR PROPIEDAD (Solo usuarios autenticados con rol HOST)
    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<?> createProperty(@Valid @RequestBody PropertyDTO propertyDTO) {
        try {
            Property property = propertyService.createProperty(propertyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(property);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error creating property",
                "message", e.getMessage()
            ));
        }
    }

    // ✅ OBTENER TODAS LAS PROPIEDADES (Público - cualquiera puede ver)
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    // ✅ OBTENER UNA PROPIEDAD POR ID (Público)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable Long id) {
        try {
            Property property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(property);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Property not found",
                "message", e.getMessage()
            ));
        }
    }

    // ✅ OBTENER PROPIEDAD CON DETALLES (Público)
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getPropertyWithDetails(@PathVariable Long id) {
        try {
            Property property = propertyService.getPropertyWithDetails(id);
            return ResponseEntity.ok(property);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Property not found",
                "message", e.getMessage()
            ));
        }
    }

    // ✅ ACTUALIZAR PROPIEDAD (Solo el propietario puede actualizar)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<?> updateProperty(
            @PathVariable Long id, 
            @Valid @RequestBody PropertyDTO propertyDTO) {
        try {
            Property property = propertyService.updateProperty(id, propertyDTO);
            return ResponseEntity.ok(property);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Property not found",
                    "message", e.getMessage()
                ));
            } else if (e.getMessage().contains("only update your own")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access denied",
                    "message", e.getMessage()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error updating property",
                    "message", e.getMessage()
                ));
            }
        }
    }

    // ✅ ELIMINAR PROPIEDAD (Solo el propietario puede eliminar)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Property not found",
                    "message", e.getMessage()
                ));
            } else if (e.getMessage().contains("only delete your own")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access denied",
                    "message", e.getMessage()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error deleting property",
                    "message", e.getMessage()
                ));
            }
        }
    }

    // ✅ OBTENER MIS PROPIEDADES (Solo usuarios autenticados)
    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<?> getMyProperties() {
        try {
            List<Property> properties = propertyService.getMyProperties();
            return ResponseEntity.ok(properties);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Authentication required",
                "message", e.getMessage()
            ));
        }
    }

    // ✅ OBTENER PROPIEDADES POR PROPIETARIO (Solo admins o el mismo usuario)
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Property>> getPropertiesByOwner(@PathVariable Long ownerId) {
        List<Property> properties = propertyService.getPropertiesByOwnerId(ownerId);
        return ResponseEntity.ok(properties);
    }

    // ✅ BÚSQUEDA POR CIUDAD/DIRECCIÓN (Público)
    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        
        // Aquí puedes implementar lógica de búsqueda más compleja
        // Por ahora devolvemos todas las propiedades
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }
}