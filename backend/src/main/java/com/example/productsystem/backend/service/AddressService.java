package com.example.productsystem.backend.service;


import com.example.productsystem.backend.entity.Address;
import com.example.productsystem.backend.repository.AddressRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

/*
 * Сервисный слой для управления сущностями Address.
 * Обрабатывает бизнес-логику, связанную с созданием, обновлением, удалением и поиском адресов.
 * Взаимодействует с репозиторием для выполнения операций с базой данных.
 */
@ApplicationScoped
public class AddressService {

    @Inject
    private AddressRepository addressRepository;

    @Transactional
    public Address create(Address address) {
        return addressRepository.create(address);
    }

    public Address find(Long id) {
        return addressRepository.find(id);
    }

    @Transactional
    public Address update(Address address) {
        return addressRepository.update(address);
    }

    @Transactional
    public void delete(Long id) {
        addressRepository.delete(id);
    }

    public List<Address> list(int page, int size, String sortField, boolean asc) {
        return addressRepository.list(page, size, sortField, asc);
    }

    public List<Address> findByZipCode(String zipCode) {
        return addressRepository.findByZipCode(zipCode);
    }

    public List<Address> findByTownName(String townName, int page, int size) {
        return addressRepository.findByTownName(townName, page, size);
    }

    public List<String> findUniqueZipCodes() {
        return addressRepository.findUniqueZipCodes();
    }

    public List<Map<String, Object>> getAddressCountByTown() {
        return addressRepository.getAddressCountByTown();
    }
}
