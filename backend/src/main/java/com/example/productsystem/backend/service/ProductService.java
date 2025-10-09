package com.example.productsystem.backend.service;

import com.example.productsystem.backend.entity.Coordinates;
import com.example.productsystem.backend.entity.Organization;
import com.example.productsystem.backend.entity.Person;
import com.example.productsystem.backend.entity.Product;
import com.example.productsystem.backend.repository.CoordinatesRepository;
import com.example.productsystem.backend.repository.OrganizationRepository;
import com.example.productsystem.backend.repository.PersonRepository;
import com.example.productsystem.backend.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/*
 * Сервисный слой для управления сущностями Product.
 * Обрабатывает бизнес-логику, связанную с созданием, обновлением, удалением и поиском продуктов.
 * Взаимодействует с репозиториями для выполнения операций с базой данных.
 */
@ApplicationScoped
public class ProductService {

    @Inject
    private ProductRepository repo;

    @Inject
    private PersonRepository personRepo;

    @Inject
    private OrganizationRepository organizationRepo;

    @Inject
    private CoordinatesRepository coordinatesRepo;

    @Transactional
    public Product create(Product product) {
        if (product.getOwner() != null) {
            product.setOwner(findOrCreatePerson(product.getOwner()));
        }

        if (product.getManufacturer() != null) {
            product.setManufacturer(findOrCreateOrganization(product.getManufacturer()));
        }

        if (product.getCoordinates() != null) {
            product.setCoordinates(findOrCreateCoordinates(product.getCoordinates()));
        }

        return repo.create(product);
    }

    public Product find(Long id) {
        return repo.find(id);
    }

    @Transactional
    public Product update(Product product) {
        if (product.getOwner() != null) {
            product.setOwner(findOrCreatePerson(product.getOwner()));
        }

        if (product.getManufacturer() != null) {
            product.setManufacturer(findOrCreateOrganization(product.getManufacturer()));
        }

        if (product.getCoordinates() != null) {
            product.setCoordinates(findOrCreateCoordinates(product.getCoordinates()));
        }

        return repo.update(product);
    }

    private Person findOrCreatePerson(Person newPerson) {
        if (newPerson.getId() != null) {
            Person existing = personRepo.find(newPerson.getId());
            if (existing != null) {
                return existing;
            }
        }

        if (newPerson.getPassportID() != null) {
            Optional<Person> existingByPassport = personRepo.findByPassportID(newPerson.getPassportID());
            if (existingByPassport.isPresent()) {
                return existingByPassport.get();
            }
        }

        return newPerson;
    }

    private Organization findOrCreateOrganization(Organization newOrg) {
        if (newOrg.getId() != null) {
            Organization existing = organizationRepo.find(newOrg.getId());
            if (existing != null) {
                return existing;
            }
        }

        if (newOrg.getName() != null) {
            List<Organization> existingByName = organizationRepo.findByName(newOrg.getName());
            if (!existingByName.isEmpty()) {
                return existingByName.get(0);
            }
        }

        return newOrg;
    }

    private Coordinates findOrCreateCoordinates(Coordinates newCoords) {
        if (newCoords.getId() != null) {
            Coordinates existing = coordinatesRepo.find(newCoords.getId());
            if (existing != null) {
                return existing;
            }
        }

        if (newCoords.getX() != null && newCoords.getY() != null) {
            List<Coordinates> existingByValues = coordinatesRepo.findByCoordinates(newCoords.getX(), newCoords.getY());
            if (!existingByValues.isEmpty()) {
                return existingByValues.get(0);
            }
        }

        return newCoords;
    }


    @Transactional
    public void delete(Long id) {
        Product product = repo.find(id);
        if (product != null) {
            product.setOwner(null);
            product.setManufacturer(null);
            product.setCoordinates(null);
            repo.update(product);
            repo.delete(product);
        }
    }

    public List<Product> list(int page, int size, String sortField, boolean asc) {
        return repo.list(page, size, sortField, asc);
    }

    public List<Product> filter(String name, Float min, Float max, int page, int size) {
        return repo.filter(name, min, max, page, size);
    }

    @Transactional
    public boolean deleteByPartNumber(String partNumber) {
        Optional<Product> p = repo.findByPartNumber(partNumber);
        if (p.isPresent()) {
            repo.delete(p.get());
            return true;
        }
        return false;
    }

    public List<Product> filterByField(String field, String value, int page, int size, String sortField, boolean asc) {
        return repo.filterByField(field, value, page, size, sortField, asc);
    }

    public List<Product> ratingGreaterThan(int rating) {
        return repo.ratingGreaterThan(rating);
    }

    public List<String> uniquePartNumbers() {
        return repo.uniquePartNumbers();
    }

    public List<Product> priceRange(Float min, Float max) {
        return repo.priceRange(min, max);
    }

    @Transactional
    public int increasePricePercent(int percent) {
        return repo.increasePricePercent(percent);
    }
}
