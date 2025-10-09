package com.example.productsystem.backend.service;

import com.example.productsystem.backend.entity.Location;
import com.example.productsystem.backend.entity.Person;
import com.example.productsystem.backend.repository.PersonRepository;
import com.example.productsystem.common.Color;
import com.example.productsystem.common.Country;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/*
 * Сервисный слой для управления сущностями Person.
 * Обрабатывает бизнес-логику, связанную с созданием, обновлением, удалением и поиском людей.
 * Взаимодействует с репозиторием PersonRepository для выполнения операций с базой данных.
 */
@ApplicationScoped
public class PersonService {

    @Inject
    private PersonRepository repo;

    @Transactional
    public Person create(Person person) {
        return repo.create(person);
    }

    public Person find(Long id) {
        return repo.find(id);
    }

    @Transactional
    public Person update(Person person) {
        return repo.update(person);
    }

    @Transactional
    public void delete(Long id) {
        Person person = repo.find(id);
        if (person != null) repo.delete(person);
    }

    public List<Person> list(int page, int size, String sortField, boolean asc) {
        return repo.list(page, size, sortField, asc);
    }

    public Optional<Person> findByPassportID(String passportID) {
        return repo.findByPassportID(passportID);
    }

    public List<Person> findByNationality(Country nationality) {
        return repo.findByNationality(nationality);
    }

    public List<Person> findByEyeColor(Color eyeColor) {
        return repo.findByEyeColor(eyeColor);
    }

    public List<Person> findByHairColor(Color hairColor) {
        return repo.findByHairColor(hairColor);
    }

    public List<Person> findByName(String name) {
        return repo.findByName(name);
    }

    public Location findLocationByPersonId(Long id) {
        return repo.findLocationByPersonId(id);
    }

    @Transactional
    public void updateLocation(Long personId, Location newLocation) {
        repo.updateLocation(personId, newLocation);
    }

    public List<Person> filterByMultipleCriteria(Color eyeColor, Color hairColor, Country nationality, int page, int size) {
        return repo.filterByMultipleCriteria(eyeColor, hairColor, nationality, page, size);
    }

    public Long countByNationality(Country nationality) {
        return repo.countByNationality(nationality);
    }

    @Transactional
    public boolean deleteByPassportID(String passportID) {
        Optional<Person> person = repo.findByPassportID(passportID);
        if (person.isPresent()) {
            repo.delete(person.get());
            return true;
        }
        return false;
    }
}
