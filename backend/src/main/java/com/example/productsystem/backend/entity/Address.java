package com.example.productsystem.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/*
* Адрес класс содержащий в себе почтовый индекс и город.
 */
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String zipCode;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Location town;




    public Address() {
    }

    public Address(String zipCode, Location town) {
        this.zipCode = zipCode;
        this.town = town;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setTown(Location town) {
        this.town = town;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Location getTown() {
        return town;
    }
}
