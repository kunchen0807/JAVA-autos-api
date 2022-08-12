package com.galvanize.autos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "automobiles ")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Automobile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "model_year")
    private int year;
    private String make;
    private String model;
    private String color;
    @Column(name = "owner_name")
    private String owner;
    @Column(unique = true)
    private String vin;

    @JsonFormat(pattern = "mm/dd/yyyy")
    private Date purchaseDate;

    public Automobile() {

    }
    public Automobile(int year, String make, String model, String vin) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.vin = vin;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "Automobile{" +
                "year='" + year + "\'" +
                ", make=" + make + "\'" +
                ", model=" + model + "\'" +
                ", color=" + color + "\'" +
                ", owner=" + owner + "\'" +
                ", vin=" + vin + "\'" +
                "}";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Automobile automobile = (Automobile) o;
        return year == automobile.year && Objects.equals(make, automobile.make)
                && Objects.equals(model, automobile.model)
                && Objects.equals(vin, automobile.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, make, model, color, owner, vin);
    }

}
