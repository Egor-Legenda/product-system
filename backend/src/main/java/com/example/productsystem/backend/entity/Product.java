package com.example.productsystem.backend.entity;

import com.example.productsystem.common.UnitOfMeasure;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/*
 * Product класс содержащий в себе информацию о продукте.
 */
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "partNumber")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Coordinates coordinates;

    @NotNull
    @Column(nullable = false, updatable = false)
    private LocalDate creationDate = LocalDate.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    @ManyToOne(cascade = CascadeType.ALL)
    private Organization manufacturer;

    @NotNull
    @Positive
    private Float price;

    private long manufactureCost;

    @Positive
    private int rating;

    @Size(min = 25)
    @Column(unique = true)
    private String partNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    private Person owner;

    public Product() {
    }

    public Product(String name, Coordinates coordinates, UnitOfMeasure unitOfMeasure, Organization manufacturer, Float price, long manufactureCost, int rating, String partNumber, Person owner) {
        this.name = name;
        this.coordinates = coordinates;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
        this.price = price;
        this.manufactureCost = manufactureCost;
        this.rating = rating;
        this.partNumber = partNumber;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Organization getManufacturer() {
        return manufacturer;
    }

    public Float getPrice() {
        return price;
    }

    public long getManufactureCost() {
        return manufactureCost;
    }

    public int getRating() {
        return rating;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public Person getOwner() {
        return owner;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setManufacturer(Organization manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setManufactureCost(long manufactureCost) {
        this.manufactureCost = manufactureCost;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}

