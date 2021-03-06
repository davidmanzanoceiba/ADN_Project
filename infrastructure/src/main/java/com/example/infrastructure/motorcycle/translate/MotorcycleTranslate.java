package com.example.infrastructure.motorcycle.translate;

import com.example.domain.vehicle.motorcycle.model.Motorcycle;
import com.example.infrastructure.motorcycle.database.entity.MotorcycleEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class MotorcycleTranslate {

    private MotorcycleTranslate() {}

    public static MotorcycleEntity translateMotorcycleFromDomainToDB(Motorcycle motorcycle) {
        MotorcycleEntity motorcycleEntity = new MotorcycleEntity();
        motorcycleEntity.setLicensePlate(motorcycle.getLicensePlate());
        motorcycleEntity.setEntryDate(motorcycle.getEntryDate().toString());
        motorcycleEntity.setCylinderCapacity(motorcycle.getCylinderCapacity());
        return motorcycleEntity;
    }

    public static Motorcycle translateMotorcycleFromDBToDomain(MotorcycleEntity motorcycleEntity) {
        LocalDateTime localDateTime = LocalDateTime.parse(motorcycleEntity.getEntryDate());
        return new Motorcycle(motorcycleEntity.getLicensePlate(), localDateTime, String.valueOf(motorcycleEntity.getCylinderCapacity()));
    }

    public static List<Motorcycle> translateMotorcycleListFromDBToDomain(List<MotorcycleEntity> motorcycleList) {
        List<Motorcycle> translatedMotorcycleList = new ArrayList<>();
        motorcycleList.forEach(motorcycle ->
                translatedMotorcycleList.add(translateMotorcycleFromDBToDomain(motorcycle)));
        return translatedMotorcycleList;
    }
}
