package com.example.domain.parking.service;

import com.example.domain.parking.exception.ParkingLimitException;
import com.example.domain.parking.exception.RestrictedAccessByDayException;
import com.example.domain.parking.model.Parking;
import com.example.domain.vehicle.car.model.Car;
import com.example.domain.vehicle.motorcycle.model.Motorcycle;
import com.example.domain.vehicle.vehicle.model.Vehicle;
import com.example.domain.vehicle.car.repository.CarRepository;
import com.example.domain.vehicle.motorcycle.repository.MotorcycleRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ParkingServiceUnitTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private MotorcycleRepository motorcycleRepository;

    private Parking parking;
    int sunday = 7;
    private ParkingService parkingService;
    private static final String RESTRICTED_ACCESS_BY_DAY = "The vehicle's license plate is restricted for today's entry.";
    private static final String PARKING_LIMIT_EXCEPTION = "The parking lot has reached its capacity limit.";

    @Before
    public void initElements() {
        carRepository = Mockito.mock(CarRepository.class);
        motorcycleRepository = Mockito.mock(MotorcycleRepository.class);
        parkingService = new ParkingService(carRepository, motorcycleRepository);
        parking = new Parking();
    }

    @Test
    public void getParkingTime_4HoursDifference_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        LocalDateTime exitDate = LocalDateTime
                .of(2021, 5, 23, 17, 57, 0);
        Vehicle vehicle = new Car("YMU-95C", entryDate);
        //Act
        int parkingTime = parkingService.getParkingTime(vehicle.getEntryDate(), exitDate);
        //Assert
        assertEquals(4, parkingTime);
    }

    @Test
    public void carParkingCost_5HoursParking_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        LocalDateTime exitDate = LocalDateTime
                .of(2021, 5, 23, 18, 57, 0);
        Car car = new Car("YMU-95C", entryDate);
        //Act
        int parkingCost = parkingService.carParkingCost(car, exitDate);
        //Assert
        assertEquals(5000, parkingCost);
    }

    @Test
    public void carParkingCost_24HoursParking_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        LocalDateTime exitDate = LocalDateTime
                .of(2021, 5, 24, 13, 57, 0);
        Car car = new Car("YMU-95C", entryDate);
        //Act
        int parkingCost = parkingService.carParkingCost(car, exitDate);
        //Assert
        assertEquals(8000, parkingCost);
    }

    @Test
    public void carParkingCost_30HoursParking_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        LocalDateTime exitDate = LocalDateTime
                .of(2021, 5, 24, 19, 57, 0);
        Car car = new Car("YMU-95C", entryDate);
        //Act
        int parkingCost = parkingService.carParkingCost(car, exitDate);
        //Assert
        assertEquals(14000, parkingCost);
    }

    @Test
    public void carParkingCost_33HoursParking_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        LocalDateTime exitDate = LocalDateTime
                .of(2021, 5, 24, 22, 57, 0);
        Car car = new Car("YMU-95C", entryDate);
        //Act
        int parkingCost = parkingService.carParkingCost(car, exitDate);
        //Assert
        assertEquals(16000, parkingCost);
    }

    @Test
    public void motorcycleParkingCost_5HoursParkingAnd500CylinderCapacity_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        LocalDateTime exitDate = LocalDateTime
                .of(2021, 5, 23, 18, 57, 0);
        Motorcycle motorcycle = new Motorcycle("YMU-95C", entryDate, "500");
        //Act
        int parkingCost = parkingService.motorcycleParkingCost(motorcycle, exitDate);
        //Assert
        assertEquals(2500, parkingCost);
    }

    @Test
    public void motorcycleParkingCost_24HoursParkingAnd650CylinderCapacity_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        LocalDateTime exitDate = LocalDateTime
                .of(2021, 5, 24, 13, 57, 0);
        Motorcycle motorcycle = new Motorcycle("YMU-95C", entryDate, "650");
        //Act
        int parkingCost = parkingService.motorcycleParkingCost(motorcycle, exitDate);
        //Assert
        assertEquals(6000, parkingCost);
    }

    @Test
    public void validateLicensePlate_startsWithAOnATuesday_isCorrect() {
        //Arrange
        int tuesday = 2;
        String licensePlate = "AMU-95C";
        //Act
        boolean accessDenied = parkingService.validateLicensePlate(licensePlate, tuesday);
        //Assert
        assertFalse(accessDenied);
    }

    @Test
    public void validateLicensePlate_startsWithAOnASunday_isCorrect() {
        //Arrange
        String licensePlate = "AMU-95C";
        //Act
        boolean accessDenied = parkingService.validateLicensePlate(licensePlate, sunday);
        //Assert
        assertTrue(accessDenied);
    }

    @Test
    public void saveCar_limitOfCarsInTheParking_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        Car car = new Car("YMU-95C", entryDate);
        when(carRepository.getNumberOfCars()).thenReturn(parking.getMaxNumberOfCars());
        //Act
        try {
            parkingService.saveCar(car, sunday);
        } catch (ParkingLimitException e) {
            //Assert
            assertEquals(PARKING_LIMIT_EXCEPTION, e.getMessage());
        }
    }

    @Test
    public void saveCar_restrictedAccessByDay_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        Car car = new Car("AMU-95C", entryDate);
        when(carRepository.getNumberOfCars()).thenReturn(7);
        //Act
        try {
            parkingService.saveCar(car, sunday);
        } catch (RestrictedAccessByDayException e) {
            //Assert
            assertEquals(RESTRICTED_ACCESS_BY_DAY, e.getMessage());
        }
    }

    @Test
    public void saveMotorcycle_limitOfMotorcyclesInTheParking_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        Motorcycle motorcycle = new Motorcycle("YMU-95C", entryDate, "500");
        when(motorcycleRepository.getNumberOfMotorcycles()).thenReturn(parking.getMaxNumberOfMotorcycles());
        //Act
        try {
            parkingService.saveMotorcycle(motorcycle, sunday);
        } catch (ParkingLimitException e) {
            //Assert
            assertEquals(PARKING_LIMIT_EXCEPTION, e.getMessage());
        }
    }

    @Test
    public void saveMotorcycle_restrictedAccessByDay_isCorrect() {
        //Arrange
        LocalDateTime entryDate = LocalDateTime
                .of(2021, 5, 23, 13, 57, 0);
        Motorcycle motorcycle = new Motorcycle("AMU-95C", entryDate, "500");
        when(motorcycleRepository.getNumberOfMotorcycles()).thenReturn(7);
        //Act
        try {
            parkingService.saveMotorcycle(motorcycle, sunday);
        } catch (RestrictedAccessByDayException e) {
            //Assert
            assertEquals(RESTRICTED_ACCESS_BY_DAY, e.getMessage());
        }
    }
}
