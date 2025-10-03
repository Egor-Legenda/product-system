package com.example.productsystem.backend.mapper;


import com.example.productsystem.backend.entity.Person;
import com.example.productsystem.common.PersonDTO;

public class PersonMapper {

    public static PersonDTO toDTO(Person entity) {
        if (entity == null) return null;
        PersonDTO dto = new PersonDTO();
        dto.setName(entity.getName());
        dto.setEyeColor(entity.getEyeColor());
        dto.setHairColor(entity.getHairColor());
        dto.setLocation(LocationMapper.toDTO(entity.getLocation()));
        dto.setPassportID(entity.getPassportID());
        dto.setNationality(entity.getNationality());
        return dto;
    }

    public static Person toEntity(PersonDTO dto) {
        if (dto == null) return null;
        Person entity = new Person();
        entity.setName(dto.getName());
        entity.setEyeColor(dto.getEyeColor());
        entity.setHairColor(dto.getHairColor());
        entity.setLocation(LocationMapper.toEntity(dto.getLocation()));
        entity.setPassportID(dto.getPassportID());
        entity.setNationality(dto.getNationality());
        return entity;
    }
}

