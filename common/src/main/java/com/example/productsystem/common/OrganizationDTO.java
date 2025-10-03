package com.example.productsystem.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class OrganizationDTO {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private AddressDTO officialAddress; //Поле не может быть null
    private Long annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    private int employeesCount; //Значение поля должно быть больше 0
    private int rating; //Значение поля должно быть больше 0
    private OrganizationType type; //Поле не может быть null
    private AddressDTO postalAddress; //Поле не может быть null

    public OrganizationDTO() {
    }

    public OrganizationDTO(String name, AddressDTO officialAddress, Long annualTurnover, int employeesCount, int rating, OrganizationType type, AddressDTO postalAddress) {
        this.name = name;
        this.officialAddress = officialAddress;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.rating = rating;
        this.type = type;
        this.postalAddress = postalAddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressDTO getOfficialAddress() {
        return officialAddress;
    }

    public void setOfficialAddress(AddressDTO officialAddress) {
        this.officialAddress = officialAddress;
    }

    public Long getAnnualTurnover() {
        return annualTurnover;
    }

    public void setAnnualTurnover(Long annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public int getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(int employeesCount) {
        this.employeesCount = employeesCount;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public AddressDTO getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(AddressDTO postalAddress) {
        this.postalAddress = postalAddress;
    }

}