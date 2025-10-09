package com.example.productsystem.backend.service;

import com.example.productsystem.backend.entity.Address;
import com.example.productsystem.backend.entity.Organization;
import com.example.productsystem.backend.repository.OrganizationRepository;
import com.example.productsystem.common.OrganizationType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/*
 * Сервисный слой для управления сущностями Organization.
 * Обрабатывает бизнес-логику, связанную с созданием, обновлением, удалением и поиском организаций.
 * Взаимодействует с репозиториями для выполнения операций с базой данных.
 */
@ApplicationScoped
public class OrganizationService {

    @Inject
    private OrganizationRepository repo;

    @Transactional
    public Organization create(Organization org) {
        return repo.create(org);
    }

    public Organization find(Integer id) {
        return repo.find(id);
    }

    @Transactional
    public Organization update(Organization org) {
        return repo.update(org);
    }

    @Transactional
    public void delete(Integer id) {
        Organization org = repo.find(id);
        if (org != null) repo.delete(org);
    }

    public List<Organization> findByName(String name) {
        return repo.findByName(name);
    }

    public List<Organization> findByType(OrganizationType type) {
        return repo.findByType(type);
    }

    public List<Organization> list(int page, int size, String sortField, boolean asc) {
        return repo.list(page, size, sortField, asc);
    }

    public List<Organization> sortByTurnover(int page, int size, boolean ascending) {
        return repo.sortByTurnover(page, size, ascending);
    }

    public Address findAddressByOrganizationId(Integer id) {
        return repo.findAddressByOrganizationId(id);
    }

    @Transactional
    public void updateAddress(Integer organizationId, Address newAddress) {
        repo.updateAddress(organizationId, newAddress);
    }

    public List<Organization> filterByEmployeeRange(int minEmployees, int maxEmployees, int page, int size) {
        return repo.filterByEmployeeRange(minEmployees, maxEmployees, page, size);
    }

    public List<Organization> findByRatingGreaterThan(int rating) {
        return repo.findByRatingGreaterThan(rating);
    }

    public Long countByType(OrganizationType type) {
        return repo.countByType(type);
    }
}
