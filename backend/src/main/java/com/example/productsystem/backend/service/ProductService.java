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
import java.util.Objects;
import java.util.Optional;

/*
 * Сервисный слой для управления сущностями Product.
 * Обрабатывает бизнес-логику, связанную с созданием, обновлением, удалением и поиском продуктов.
 * Взаимодействует с репозиториями для выполнения операций с базой данных.
 */
@ApplicationScoped
public class ProductService {

    @Inject
    private ProductRepository productRepo;

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

        return productRepo.create(product);
    }

    public Product find(Long id) {
        return productRepo.find(id);
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

        return productRepo.update(product);
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
                Person existing = existingByPassport.get();
                if (isPersonEqual(existing, newPerson)) {
                    return existing;
                }
            }
        }

        List<Person> allPersons = personRepo.list(0, Integer.MAX_VALUE, null, true);
        for (Person existing : allPersons) {
            if (isPersonEqual(existing, newPerson)) {
                return existing;
            }
        }

        return newPerson;
    }

    private boolean isPersonEqual(Person existing, Person newPerson) {
        return Objects.equals(existing.getName(), newPerson.getName()) &&
                Objects.equals(existing.getPassportID(), newPerson.getPassportID()) &&
                existing.getNationality().equals(newPerson.getNationality()) &&
                existing.getEyeColor().equals(newPerson.getEyeColor()) &&
                existing.getHairColor().equals(newPerson.getHairColor());
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
            for (Organization existing : existingByName) {
                if (isOrganizationEqual(existing, newOrg)) {
                    return existing;
                }
            }
        }

        List<Organization> allOrganizations = organizationRepo.list(0, Integer.MAX_VALUE, null, true);
        for (Organization existing : allOrganizations) {
            if (isOrganizationEqual(existing, newOrg)) {
                return existing;
            }
        }

        return newOrg;
    }

    private boolean isOrganizationEqual(Organization existing, Organization newOrg) {
        return Objects.equals(existing.getName(), newOrg.getName()) &&
                existing.getType() == newOrg.getType() &&
                Objects.equals(existing.getAnnualTurnover(), newOrg.getAnnualTurnover()) &&
                existing.getEmployeesCount() == newOrg.getEmployeesCount() &&
                existing.getRating() == newOrg.getRating();
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
            for (Coordinates existing : existingByValues) {
                if (isCoordinatesEqual(existing, newCoords)) {
                    return existing;
                }
            }
        }

        List<Coordinates> allCoordinates = coordinatesRepo.list(0, Integer.MAX_VALUE, null, true);
        for (Coordinates existing : allCoordinates) {
            if (isCoordinatesEqual(existing, newCoords)) {
                return existing;
            }
        }

        return newCoords;
    }

    private boolean isCoordinatesEqual(Coordinates existing, Coordinates newCoords) {
        return Objects.equals(existing.getX(), newCoords.getX()) &&
                Objects.equals(existing.getY(), newCoords.getY());
    }


    @Transactional
    public void delete(Long id) {
        Product product = productRepo.find(id);
        if (product != null) {
            product.setOwner(null);
            product.setManufacturer(null);
            product.setCoordinates(null);
            productRepo.update(product);
            productRepo.delete(product);
        }
    }

    public List<Product> list(int page, int size, String sortField, boolean asc) {
        return productRepo.list(page, size, sortField, asc);
    }

    public List<Product> filter(String name, Float min, Float max, int page, int size) {
        return productRepo.filter(name, min, max, page, size);
    }

    @Transactional
    public boolean deleteByPartNumber(String partNumber) {
        Optional<Product> p = productRepo.findByPartNumber(partNumber);
        if (p.isPresent()) {
            Product product = p.get();
            product.setOwner(null);
            product.setManufacturer(null);
            product.setCoordinates(null);
            productRepo.update(product);
            productRepo.delete(product);
            return true;
        }
        return false;
    }

    public List<Product> filterByField(String field, String value, int page, int size, String sortField, boolean asc) {
        return productRepo.filterByField(field, value, page, size, sortField, asc);
    }

    public List<Product> ratingGreaterThan(int rating) {
        return productRepo.ratingGreaterThan(rating);
    }

    public List<String> uniquePartNumbers() {
        return productRepo.uniquePartNumbers();
    }

    public List<Product> priceRange(Float min, Float max) {
        return productRepo.priceRange(min, max);
    }

    @Transactional
    public int increasePricePercent(int percent) {
        return productRepo.increasePricePercent(percent);
    }
}
