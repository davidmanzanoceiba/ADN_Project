package com.example.domain.exception;

public class RestrictedAccessByDayException extends RuntimeException{

    public static final String RESTRICTED_ACCESS_BY_DAY = "The vehicle's license plate is restricted for today's entry.";

    public RestrictedAccessByDayException() {
        super(RESTRICTED_ACCESS_BY_DAY);
    }
}
