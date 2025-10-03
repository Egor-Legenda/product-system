package com.example.productsystem.backend.mapper;

import com.example.productsystem.backend.entity.Location;
import com.example.productsystem.common.LocationDTO;

public class LocationMapper {
    public static LocationDTO toDTO(Location entity) {
        if (entity == null) return null;
        LocationDTO dto = new LocationDTO();
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        dto.setZ(entity.getZ());
        dto.setName(entity.getName());
        return dto;
    }

    public static Location toEntity(LocationDTO dto) {
        if (dto == null) return null;
        com.example.productsystem.backend.entity.Location entity =
                new com.example.productsystem.backend.entity.Location();
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setZ(dto.getZ());
        entity.setName(dto.getName());
        return entity;
    }
}
