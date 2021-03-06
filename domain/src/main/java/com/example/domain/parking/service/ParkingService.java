package com.example.domain.parking.service;

import com.example.domain.parking.exception.ParkingLimitException;
import com.example.domain.parking.exception.RestrictedAccessByDayException;
import com.example.domain.parking.model.Parking;
import com.example.domain.vehicle.car.model.Car;
import com.example.domain.vehicle.motorcycle.model.Motorcycle;
import com.example.domain.parking.model.Rate;
import com.example.domain.vehicle.vehicle.model.Vehicle;
import com.example.domain.vehicle.car.repository.CarRepository;
import com.example.domain.vehicle.motorcycle.repository.MotorcycleRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ParkingService {

    private final CarRepository carRepository;
    private final MotorcycleRepository motorcycleRepository;
    private static final float MILLISECONDS_IN_AN_HOUR = 3600000;
    private static final int HOURS_IN_A_DAY = 24;
    private final Parking parking;

    @Inject
    public ParkingService(CarRepository carRepository, MotorcycleRepository motorcycleRepository) {
        this.carRepository = carRepository;
        this.motorcycleRepository = motorcycleRepository;
        this.parking = new Parking();
    }

    public void saveCar(Car car, int currentDay) {
        int numberOfCars = carRepository.getNumberOfCars();
        if (numberOfCars == parking.getMaxNumberOfCars()) {
            throw new ParkingLimitException();
        } else if (validateLicensePlate(car.getLicensePlate(), currentDay)) {
            throw new RestrictedAccessByDayException();
        } else {
            carRepository.saveCar(car);
        }
    }

    public void saveMotorcycle(Motorcycle motorcycle, int currentDay) {
        int numberOfMotorcycles = motorcycleRepository.getNumberOfMotorcycles();
        if (numberOfMotorcycles == parking.getMaxNumberOfMotorcycles()) {
            throw new ParkingLimitException();
        } else if (validateLicensePlate(motorcycle.getLicensePlate(), currentDay)) {
            throw new RestrictedAccessByDayException();
        } else {
            motorcycleRepository.saveMotorcycle(motorcycle);
        }
    }

    public boolean validateLicensePlate(String licensePlate, int currentDay) {
        return (licensePlate.startsWith(parking.getFirstLetterLicensePlate())
                && (currentDay == parking.getSunday() || currentDay == parking.getMonday()));
    }

    public void deleteCar(Car car) {
        carRepository.deleteCar(car);
    }

    public void deleteMotorcycle(Motorcycle motorcycle) {
        motorcycleRepository.deleteMotorcycle(motorcycle);
    }

    public List<Vehicle> getVehicles() {
        List<Vehicle> vehicleList = new ArrayList<>();
        vehicleList.addAll(carRepository.getCars());
        vehicleList.addAll(motorcycleRepository.getMotorcycles());
        return vehicleList;
    }

    public int carParkingCost(Car car, LocalDateTime exitDate) {
        Rate carRate = parking.getCarRate();
        return calculateParkingCost(car, exitDate, carRate);
    }

    public int motorcycleParkingCost(Motorcycle motorcycle, LocalDateTime exitDate) {
        Rate motorcycleRate = parking.getMotorcycleRate();
        int parkingCost = calculateParkingCost(motorcycle, exitDate, motorcycleRate);
        if (motorcycle.getCylinderCapacity() > parking.getCylinderCapacityLimit()) {
            parkingCost += motorcycleRate.getSurplus();
        }
        return parkingCost;
    }

    public int calculateParkingCost(Vehicle vehicle, LocalDateTime exitDate, Rate vehicleRate) {
        int hourLimit = parking.getHourLimit();
        int priceHour = vehicleRate.getPriceHour();
        int priceDay = vehicleRate.getPriceDay();
        int parkingCost;
        int parkingTime = getParkingTime(vehicle.getEntryDate(), exitDate);
        if (parkingTime < hourLimit) {
            parkingCost = parkingTime * priceHour;
        } else {
            int days = parkingTime / HOURS_IN_A_DAY;
            int hours = parkingTime % HOURS_IN_A_DAY;
            if (hours >= hourLimit) {
                days += 1;
                parkingCost = days * priceDay;
            } else {
                parkingCost = (days * priceDay) + (hours * priceHour);
            }
        }
        return parkingCost;
    }

    public int getParkingTime(LocalDateTime entryDate, LocalDateTime exitDate) {
        long timeElapsed = entryDate.until(exitDate, ChronoUnit.MILLIS);
        return (int) Math.ceil(timeElapsed / MILLISECONDS_IN_AN_HOUR);
    }

}
