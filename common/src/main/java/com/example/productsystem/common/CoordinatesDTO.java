package com.example.productsystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class CoordinatesDTO {
    private Integer x; //Значение поля должно быть больше -788, Поле не может быть null
    private Double y; //Поле не может быть null

    public CoordinatesDTO(Integer x, Double y) {
        this.x = x;
        this.y = y;
    }

    public CoordinatesDTO() {
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

}