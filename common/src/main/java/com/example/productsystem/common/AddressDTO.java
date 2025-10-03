package com.example.productsystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Адресс местоположения
 */

public class AddressDTO {
    private String zipCode; //Поле не может быть null
    private LocationDTO town; //Поле не может быть null

    public AddressDTO() {
    }

    public AddressDTO(String zipCode, LocationDTO town) {
        this.zipCode = zipCode;
        this.town = town;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public LocationDTO getTown() {
        return town;
    }

    public void setTown(LocationDTO town) {
        this.town = town;
    }

}
