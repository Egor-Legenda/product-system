package com.example.productsystem.backend.mapper;


import com.example.productsystem.backend.entity.Product;
import com.example.productsystem.common.ProductDTO;

/*
 * Маппер для преобразования между Product и ProductDTO.
 */
public class ProductMapper {

    public static ProductDTO toDTO(Product entity) {
        if (entity == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPartNumber(entity.getPartNumber());
        dto.setPrice(entity.getPrice());
        dto.setRating(entity.getRating());
        dto.setCreationDate(entity.getCreationDate());
        dto.setUnitOfMeasure(entity.getUnitOfMeasure());
        dto.setCoordinates(CoordinatesMapper.toDTO(entity.getCoordinates()));
        dto.setManufacturer(OrganizationMapper.toDTO(entity.getManufacturer()));
        dto.setOwner(PersonMapper.toDTO(entity.getOwner()));
        return dto;
    }

    public static Product toEntity(ProductDTO dto) {
        if (dto == null) return null;
        Product entity = new Product();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPartNumber(dto.getPartNumber());
        entity.setPrice(dto.getPrice());
        entity.setRating(dto.getRating());
        entity.setCreationDate(dto.getCreationDate());
        entity.setUnitOfMeasure(dto.getUnitOfMeasure());
        entity.setCoordinates(CoordinatesMapper.toEntity(dto.getCoordinates()));
        entity.setManufacturer(OrganizationMapper.toEntity(dto.getManufacturer()));
        entity.setOwner(PersonMapper.toEntity(dto.getOwner()));
        return entity;
    }
}

