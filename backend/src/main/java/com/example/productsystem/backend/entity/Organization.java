package com.example.productsystem.backend.entity;

import com.example.productsystem.common.OrganizationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


/*
 * Organization класс содержащий в себе информацию об организации.
 */
@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Address officialAddress;

    @NotNull
    @Positive
    private Long annualTurnover;

    @Positive
    private int employeesCount;

    @Positive
    private int rating;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrganizationType type;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Address postalAddress;

    public Organization() {
    }

    public Organization(String name, Address officialAddress, Long annualTurnover, int employeesCount, int rating, OrganizationType type, Address postalAddress) {
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

    public Address getOfficialAddress() {
        return officialAddress;
    }

    public void setOfficialAddress(Address officialAddress) {
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

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }

}

