package com.backend.bankingapp.models.utils;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Address {

    private String country;
    private String city;
    private String street;
    private int streetNum;
    private int zipCode;

    public Address(String country, String city) {
        setCountry(country);
        setCity(city);
    }

    public Address(String country, String city, String street, int streetNum, int zipCode) {
        setCountry(country);
        setCity(city);
        setStreet(street);
        setStreetNum(streetNum);
        setZipCode(zipCode);
    }
}