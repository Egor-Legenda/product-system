package com.example.productsystem.backend.mapper;

import com.example.productsystem.backend.entity.Address;
import com.example.productsystem.common.AddressDTO;

/*
* Маппер для преобразования между Address и AddressDTO.
 */
public class AddressMapper {
    public static AddressDTO toDTO(Address entity) {
        if (entity == null) return null;
        AddressDTO dto = new AddressDTO();
        dto.setZipCode(entity.getZipCode());
        dto.setTown(LocationMapper.toDTO(entity.getTown()));
        return dto;
    }

    public static Address toEntity(AddressDTO dto) {
        if (dto == null) return null;
        com.example.productsystem.backend.entity.Address entity =
                new com.example.productsystem.backend.entity.Address();
        entity.setZipCode(dto.getZipCode());
        entity.setTown(LocationMapper.toEntity(dto.getTown()));
        return entity;
    }
}

