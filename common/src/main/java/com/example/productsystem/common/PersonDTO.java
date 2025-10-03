package com.example.productsystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class PersonDTO {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Color eyeColor; //Поле не может быть null
    private Color hairColor; //Поле не может быть null
    private LocationDTO location; //Поле не может быть null
    private String passportID; //Строка не может быть пустой, Поле не может быть null
    private Country nationality; //Поле может быть null

    public PersonDTO() {
    }

    public PersonDTO(String name, Color eyeColor, Color hairColor, LocationDTO location, String passportID, Country nationality) {
        this.name = name;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
        this.passportID = passportID;
        this.nationality = nationality;
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

    public LocationDTO getLocation() {
        return location;
    }

    public String getPassportID() {
        return passportID;
    }

    public Country getNationality() {
        return nationality;
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

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }
}