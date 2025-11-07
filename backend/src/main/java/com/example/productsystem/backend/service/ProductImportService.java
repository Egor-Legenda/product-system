package com.example.productsystem.backend.service;

import com.example.productsystem.backend.entity.*;
import com.example.productsystem.backend.repository.ProductRepository;
import com.example.productsystem.common.Color;
import com.example.productsystem.common.Country;
import com.example.productsystem.common.OrganizationType;
import com.example.productsystem.common.UnitOfMeasure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProductImportService {

    @Inject
    private ProductService productService;

    @Inject
    private ProductRepository productRepository;



    @Transactional
    public int importProduct(InputStream fileInputStream, String fileName) {
        if (!fileName.endsWith(".csv")) {
            throw new ValidationException("Неверный формат файла");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String line;
            line = reader.readLine();
            String[] fields;
            if (line != null) {
                fields = line.split(",");

            } else {
                throw new ValidationException("Отсутствует заголовок сверху");
            }

            List<Product> products = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                Product product = parseLineToProduct(fields, line);
                products.add(product);

            }
            for (Product product : products) {
                productService.create(product);
            }
            return products.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Product parseLineToProduct(String[] headers, String csvLine) {
        String[] values = parseCSVLine(csvLine);
        Product product = new Product();
        product.setCreationDate(LocalDate.now());

        for (int i = 0; i < headers.length && i < values.length; i++) {
            String fieldName = headers[i];
            String stringValue = values[i];

            if (stringValue != null && !stringValue.trim().isEmpty()) {
                setProductField(product, fieldName, stringValue);
            }
        }

        validateProduct(product);

        return product;
    }

    private void setProductField(Product product, String fieldName, String stringValue) {
        try {
            switch (fieldName) {
                case "name":
                    product.setName(stringValue);
                    break;
                case "creationDate":
                    product.setCreationDate(LocalDate.parse(stringValue));
                    break;
                case "price":
                    product.setPrice(Float.parseFloat(stringValue));
                    break;
                case "manufactureCost":
                    product.setManufactureCost(Long.parseLong(stringValue));
                    break;
                case "rating":
                    product.setRating(Integer.parseInt(stringValue));
                    break;
                case "partNumber":
                    product.setPartNumber(stringValue);
                    break;
                case "unitOfMeasure":
                    product.setUnitOfMeasure(UnitOfMeasure.valueOf(stringValue.toUpperCase()));
                    break;

                case "coordinates.x":
                    if (product.getCoordinates() == null) {
                        product.setCoordinates(new Coordinates());
                    }
                    product.getCoordinates().setX(Integer.parseInt(stringValue));
                    break;
                case "coordinates.y":
                    if (product.getCoordinates() == null) {
                        product.setCoordinates(new Coordinates());
                    }
                    product.getCoordinates().setY(Double.parseDouble(stringValue));
                    break;

                case "manufacturer.name":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    product.getManufacturer().setName(stringValue);
                    break;
                case "manufacturer.officialAddress.zipCode":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getOfficialAddress() == null) {
                        product.getManufacturer().setOfficialAddress(new Address());
                    }
                    product.getManufacturer().getOfficialAddress().setZipCode(stringValue);
                    break;
                case "manufacturer.officialAddress.town.x":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getOfficialAddress() == null) {
                        product.getManufacturer().setOfficialAddress(new Address());
                    }
                    if (product.getManufacturer().getOfficialAddress().getTown() == null) {
                        product.getManufacturer().getOfficialAddress().setTown(new Location());
                    }
                    product.getManufacturer().getOfficialAddress().getTown().setX(Long.parseLong(stringValue));
                    break;
                case "manufacturer.officialAddress.town.y":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getOfficialAddress() == null) {
                        product.getManufacturer().setOfficialAddress(new Address());
                    }
                    if (product.getManufacturer().getOfficialAddress().getTown() == null) {
                        product.getManufacturer().getOfficialAddress().setTown(new Location());
                    }
                    product.getManufacturer().getOfficialAddress().getTown().setY(Double.parseDouble(stringValue));
                    break;
                case "manufacturer.officialAddress.town.z":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getOfficialAddress() == null) {
                        product.getManufacturer().setOfficialAddress(new Address());
                    }
                    if (product.getManufacturer().getOfficialAddress().getTown() == null) {
                        product.getManufacturer().getOfficialAddress().setTown(new Location());
                    }
                    product.getManufacturer().getOfficialAddress().getTown().setZ(Float.parseFloat(stringValue));
                    break;
                case "manufacturer.officialAddress.town.name":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getOfficialAddress() == null) {
                        product.getManufacturer().setOfficialAddress(new Address());
                    }
                    if (product.getManufacturer().getOfficialAddress().getTown() == null) {
                        product.getManufacturer().getOfficialAddress().setTown(new Location());
                    }
                    product.getManufacturer().getOfficialAddress().getTown().setName(stringValue);
                    break;
                case "manufacturer.postalAddress.town.x":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getPostalAddress() == null) {
                        product.getManufacturer().setPostalAddress(new Address());
                    }
                    if (product.getManufacturer().getPostalAddress().getTown() == null) {
                        product.getManufacturer().getPostalAddress().setTown(new Location());
                    }
                    product.getManufacturer().getPostalAddress().getTown().setX(Long.parseLong(stringValue));
                    break;

                case "manufacturer.postalAddress.town.y":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getPostalAddress() == null) {
                        product.getManufacturer().setPostalAddress(new Address());
                    }
                    if (product.getManufacturer().getPostalAddress().getTown() == null) {
                        product.getManufacturer().getPostalAddress().setTown(new Location());
                    }
                    product.getManufacturer().getPostalAddress().getTown().setY(Double.parseDouble(stringValue));
                    break;

                case "manufacturer.postalAddress.town.z":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getPostalAddress() == null) {
                        product.getManufacturer().setPostalAddress(new Address());
                    }
                    if (product.getManufacturer().getPostalAddress().getTown() == null) {
                        product.getManufacturer().getPostalAddress().setTown(new Location());
                    }
                    product.getManufacturer().getPostalAddress().getTown().setZ(Float.parseFloat(stringValue));
                    break;

                case "manufacturer.postalAddress.town.name":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getPostalAddress() == null) {
                        product.getManufacturer().setPostalAddress(new Address());
                    }
                    if (product.getManufacturer().getPostalAddress().getTown() == null) {
                        product.getManufacturer().getPostalAddress().setTown(new Location());
                    }
                    product.getManufacturer().getPostalAddress().getTown().setName(stringValue);
                    break;

                case "manufacturer.annualTurnover":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    product.getManufacturer().setAnnualTurnover(Long.parseLong(stringValue));
                    break;

                case "manufacturer.employeesCount":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    product.getManufacturer().setEmployeesCount(Integer.parseInt(stringValue));
                    break;

                case "manufacturer.rating":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    product.getManufacturer().setRating(Integer.parseInt(stringValue));
                    break;

                case "manufacturer.type":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    product.getManufacturer().setType(OrganizationType.valueOf(stringValue.toUpperCase()));
                    break;

                case "manufacturer.postalAddress.zipCode":
                    if (product.getManufacturer() == null) {
                        product.setManufacturer(new Organization());
                    }
                    if (product.getManufacturer().getPostalAddress() == null) {
                        product.getManufacturer().setPostalAddress(new Address());
                    }
                    product.getManufacturer().getPostalAddress().setZipCode(stringValue);
                    break;

                case "owner.name":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    product.getOwner().setName(stringValue);
                    break;

                case "owner.passportID":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    product.getOwner().setPassportID(stringValue);
                    break;

                case "owner.eyeColor":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    product.getOwner().setEyeColor(Color.valueOf(stringValue.toUpperCase()));
                    break;
                case "owner.hairColor":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    product.getOwner().setHairColor(Color.valueOf(stringValue.toUpperCase()));
                    break;
                case "owner.nationality":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    product.getOwner().setNationality(Country.valueOf(stringValue.toUpperCase()));
                    break;

                case "owner.location.x":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    if (product.getOwner().getLocation() == null) {
                        product.getOwner().setLocation(new Location());
                    }
                    product.getOwner().getLocation().setX(Long.parseLong(stringValue));
                    break;
                case "owner.location.y":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    if (product.getOwner().getLocation() == null) {
                        product.getOwner().setLocation(new Location());
                    }
                    product.getOwner().getLocation().setY(Double.parseDouble(stringValue));
                    break;
                case "owner.location.z":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    if (product.getOwner().getLocation() == null) {
                        product.getOwner().setLocation(new Location());
                    }
                    product.getOwner().getLocation().setZ(Float.parseFloat(stringValue));
                    break;
                case "owner.location.name":
                    if (product.getOwner() == null) {
                        product.setOwner(new Person());
                    }
                    if (product.getOwner().getLocation() == null) {
                        product.getOwner().setLocation(new Location());
                    }
                    product.getOwner().getLocation().setName(stringValue);
                    break;

                default:

                    throw new ValidationException("Заголовок невалидный");
            }
        } catch (Exception e) {
            throw new ValidationException(
                    "Ошибка установки поля '" + fieldName + "' со значением '" + stringValue + "': " + e.getMessage());
        }
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim().replaceAll("^\"|\"$", ""));
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim().replaceAll("^\"|\"$", ""));

        return result.toArray(new String[0]);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ValidationException("Поле 'name' обязательно");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new ValidationException("Поле 'price' должно быть больше 0");
        }
        if (product.getCoordinates() == null) {
            throw new ValidationException("Поле 'coordinates' обязательно");
        }
        if (product.getUnitOfMeasure() == null) {
            throw new ValidationException("Поле 'unitOfMeasure' обязательно");
        }
    }
}

