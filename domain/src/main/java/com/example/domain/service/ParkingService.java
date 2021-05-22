package com.example.domain.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.domain.model.Car;
import com.example.domain.model.Motorcycle;
import com.example.domain.model.Rate;
import com.example.domain.model.Vehicle;
import com.example.domain.repository.CarRepository;
import com.example.domain.repository.MotorcycleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ParkingService {

    private CarRepository carRepository;
    private MotorcycleRepository motorcycleRepository;
    private final String FIRST_LETTER_LICENSE_PLATE = "A";
    private final int SUNDAY = 7;
    private final int MONDAY = 1;
    private final int MAX_NUMBER_OF_CARS = 20;
    private final int MAX_NUMBER_OF_MOTORCYCLES = 10;
    private final float MILLISECONDS_IN_AN_HOUR = 3600000;
    private final int HOURS_IN_A_DAY = 24;
    private final int HOUR_LIMIT = 9;
    private final int CYLINDER_CAPACITY_LIMIT = 500;

    public ParkingService(CarRepository carRepository, MotorcycleRepository motorcycleRepository) {
        this.carRepository = carRepository;
        this.motorcycleRepository = motorcycleRepository;
    }

    public void saveCar(Car car) {
        int numberOfCars = carRepository.getNumberOfCars();
        int currentDay = LocalDate.now().getDayOfWeek().getValue();
        if (numberOfCars == MAX_NUMBER_OF_CARS) {
            //Exception
        } else if (validateLicensePlate(car.getLicensePlate(), currentDay)) {
            //Exception
        } else {
            carRepository.saveCar(car);
        }
    }

    public void saveMotorcycle(Motorcycle motorcycle) {
        int numberOfMotorcycles = motorcycleRepository.getNumberOfMotorcycles();
        int currentDay = LocalDate.now().getDayOfWeek().getValue();
        if (numberOfMotorcycles == MAX_NUMBER_OF_MOTORCYCLES) {
            //Exception
        } else if (validateLicensePlate(motorcycle.getLicensePlate(), currentDay)) {
            //Exception
        } else {
            motorcycleRepository.saveMotorcycle(motorcycle);
        }
    }

    public boolean validateLicensePlate(String licensePlate, int currentDay) {
        return (licensePlate.startsWith(FIRST_LETTER_LICENSE_PLATE) && (currentDay == SUNDAY || currentDay == MONDAY));
    }

    public void deleteCar(Car car) {
        carRepository.deleteCar(car);
    }

    public void deleteMotorcycle(Motorcycle motorcycle) {
        motorcycleRepository.deleteMotorcycle(motorcycle);
    }

    public int carParkingCost(Car car) {
        return calculateParkingCost(car);
    }

    public int motorcycleParkingCost(Motorcycle motorcycle) {
        int parkingCost = calculateParkingCost(motorcycle);
        if (motorcycle.getCylinderCapacity() > CYLINDER_CAPACITY_LIMIT) {
            parkingCost += motorcycle.getRATE().getSurplus();
        }
        return parkingCost;
    }

    private int calculateParkingCost(Vehicle vehicle) {
        Rate vehicleRate = vehicle.getRATE();
        int priceHour = vehicleRate.getPriceHour();
        int priceDay = vehicleRate.getPriceDay();
        int parkingCost;
        int parkingTime = getParkingTime(vehicle.getEntryDate());
        if (parkingTime < HOUR_LIMIT) {
            parkingCost = parkingTime * priceHour;
        } else {
            int days = parkingTime / HOURS_IN_A_DAY;
            int hours = parkingTime % HOURS_IN_A_DAY;
            if (hours >= HOUR_LIMIT) {
                days += 1;
                parkingCost = days * priceDay;
            } else {
                parkingCost = (days * priceDay) + (hours * priceHour);
            }
        }
        return parkingCost;
    }

    private int getParkingTime(LocalDateTime entryDate) {
        LocalDateTime exitDate = LocalDateTime.now();
        long timeElapsed = entryDate.until(exitDate, ChronoUnit.MILLIS);
        return (int) Math.ceil(timeElapsed / MILLISECONDS_IN_AN_HOUR);
    }

}
