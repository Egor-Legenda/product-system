package com.example.productsystem.backend.mapper;

import com.example.productsystem.backend.entity.Coordinates;
import com.example.productsystem.common.CoordinatesDTO;

public class CoordinatesMapper {
    public static CoordinatesDTO toDTO(
            Coordinates entity) {
        if (entity == null) return null;
        CoordinatesDTO dto = new CoordinatesDTO();
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        return dto;
    }

    public static Coordinates toEntity(
            CoordinatesDTO dto) {
        if (dto == null) return null;
        Coordinates entity = new Coordinates();
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        return entity;
    }
}

