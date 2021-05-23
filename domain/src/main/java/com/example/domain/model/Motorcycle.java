package com.example.domain.model;

import java.time.LocalDateTime;

public class Motorcycle extends Vehicle {

    private int cylinderCapacity;
    private final Rate RATE = new Rate(500, 4000, 2000);

    public Motorcycle(String licensePlate, LocalDateTime entryDate, int cylinderCapacity) {
        super(licensePlate, entryDate);
        this.cylinderCapacity = cylinderCapacity;
    }

    public int getCylinderCapacity() {
        return cylinderCapacity;
    }

    public void setCylinderCapacity(int cylinderCapacity) {
        this.cylinderCapacity = cylinderCapacity;
    }

    @Override
    public Rate getRATE() {
        return RATE;
    }
}