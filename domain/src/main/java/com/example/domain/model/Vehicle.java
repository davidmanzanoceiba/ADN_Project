package com.example.domain.model;
import java.time.LocalDateTime;

public class Vehicle {

    private String licensePlate;
    private LocalDateTime entryDate;
    private final Rate RATE = new Rate(0, 0, 0);

    public Vehicle(String licensePlate, LocalDateTime entryDate) {
        setLicensePlate(licensePlate);
        setEntryDate(entryDate);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }

    public Rate getRATE() {
        return RATE;
    }
}
