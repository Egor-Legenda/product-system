package com.example.productsystem.backend.mapper;



import com.example.productsystem.backend.entity.Organization;
import com.example.productsystem.common.OrganizationDTO;

public class OrganizationMapper {

    public static OrganizationDTO toDTO(Organization entity) {
        if (entity == null) return null;
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setOfficialAddress(AddressMapper.toDTO(entity.getOfficialAddress()));
        dto.setAnnualTurnover(entity.getAnnualTurnover());
        dto.setEmployeesCount(entity.getEmployeesCount());
        dto.setRating(entity.getRating());
        dto.setType(entity.getType());
        dto.setPostalAddress(AddressMapper.toDTO(entity.getPostalAddress()));
        return dto;
    }

    public static Organization toEntity(OrganizationDTO dto) {
        if (dto == null) return null;
        com.example.productsystem.backend.entity.Organization entity =
                new com.example.productsystem.backend.entity.Organization();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setOfficialAddress(AddressMapper.toEntity(dto.getOfficialAddress()));
        entity.setAnnualTurnover(dto.getAnnualTurnover());
        entity.setEmployeesCount(dto.getEmployeesCount());
        entity.setRating(dto.getRating());
        entity.setType(dto.getType());
        entity.setPostalAddress(AddressMapper.toEntity(dto.getPostalAddress()));
        return entity;
    }
}
