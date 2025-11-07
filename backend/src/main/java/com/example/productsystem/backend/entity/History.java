package com.example.productsystem.backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean status;

    private String useName;

    @Min(0)
    private Integer countObjects;

    public Long getId() {
        return id;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getUseName() {
        return useName;
    }

    public Integer getCountObjects() {
        return countObjects;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public void setCountObjects(Integer countObjects) {
        this.countObjects = countObjects;
    }
}
