package com.example.domain.exception;

public class ParkingLimitException extends RuntimeException{

    public static final String PARKING_LIMIT = "The parking lot has reached its capacity limit.";

    public ParkingLimitException() {
        super(PARKING_LIMIT);
    }
}