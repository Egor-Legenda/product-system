package com.example.productsystem.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public class ProductDTO {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private CoordinatesDTO coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private OrganizationDTO manufacturer; //Поле может быть null
    private Float price; //Поле не может быть null, Значение поля должно быть больше 0
    private long manufactureCost;
    private int rating; //Значение поля должно быть больше 0
    private String partNumber; //Значение этого поля должно быть уникальным, Длина строки должна быть не меньше 25, Поле может быть null
    private PersonDTO owner; //Поле может быть null

    public ProductDTO() {
    }

    public ProductDTO(String name, CoordinatesDTO coordinates, java.time.LocalDate creationDate, UnitOfMeasure unitOfMeasure, OrganizationDTO manufacturer, Float price, long manufactureCost, int rating, String partNumber, PersonDTO owner) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
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

    public CoordinatesDTO getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public OrganizationDTO getManufacturer() {
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

    public PersonDTO getOwner() {
        return owner;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(CoordinatesDTO coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setManufacturer(OrganizationDTO manufacturer) {
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

    public void setOwner(PersonDTO owner) {
        this.owner = owner;
    }
}