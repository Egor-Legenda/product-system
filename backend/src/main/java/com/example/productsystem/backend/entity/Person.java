package com.example.productsystem.backend.entity;

import com.example.productsystem.common.Color;
import com.example.productsystem.common.Country;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/*
 * Person класс содержащий в себе информацию о человеке.
 */
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Color eyeColor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Color hairColor;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Location location;

    @NotBlank
    private String passportID;

    @Enumerated(EnumType.STRING)
    private Country nationality;


    public Person() {
    }

    public Person(String name, Color eyeColor, Color hairColor, Location location, String passportID, Country nationality) {
        this.name = name;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
        this.passportID = passportID;
        this.nationality = nationality;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Location getLocation() {
        return location;
    }

    public String getPassportID() {
        return passportID;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }
}

