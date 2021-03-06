package com.example.adn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.adn.adapter.VehicleAdapter;
import com.example.adn.databinding.ActivityParkingServiceBinding;
import com.example.adn.viewmodel.ParkingViewModel;
import com.example.domain.parking.exception.GlobalException;
import com.example.domain.vehicle.car.model.Car;
import com.example.domain.vehicle.motorcycle.model.Motorcycle;
import com.example.domain.vehicle.vehicle.model.Vehicle;

import java.time.LocalDateTime;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ParkingServiceActivity extends AppCompatActivity {

    private VehicleAdapter vehicleAdapter;

    private ParkingViewModel parkingViewModel;
    private ActivityParkingServiceBinding binding;
    private boolean motorcycleType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingServiceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initElements();
        onClickManager();
        validateSavedInstance(savedInstanceState);
    }

    private void validateSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            motorcycleType = savedInstanceState.getBoolean("motorcycleType");
            activateCylinderCapacityEditText();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("motorcycleType", motorcycleType);
    }

    private void initElements() {
        parkingViewModel = new ViewModelProvider(this).get(ParkingViewModel.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerViewVehicles.setLayoutManager(linearLayoutManager);
        parkingViewModel.getVehicleMutableList().observe(this, this::updateAdapter);
    }

    private void updateAdapter(List<Vehicle> vehicleList) {
        vehicleAdapter = new VehicleAdapter(vehicleList, this);
        binding.recyclerViewVehicles.setAdapter(vehicleAdapter);
    }

    private void onClickManager() {
        binding.radioButtonMotorcycle.setOnClickListener(v -> {
            motorcycleType = true;
            activateCylinderCapacityEditText();
        });
        binding.radioButtonCar.setOnClickListener(v -> {
            motorcycleType = false;
            activateCylinderCapacityEditText();
        });
        binding.buttonSaveVehicle.setOnClickListener(v -> saveVehicle());
    }

    private void activateCylinderCapacityEditText(){
        if (motorcycleType){
            binding.radioButtonMotorcycle.setChecked(true);
            binding.editTextCylinderCapacity.setVisibility(View.VISIBLE);
        } else {
            binding.editTextCylinderCapacity.setVisibility(View.GONE);
            binding.radioButtonCar.setChecked(true);
        }
    }

    private void saveVehicle() {
        String cylinderCapacity = binding.editTextCylinderCapacity.getText().toString();
        String licensePlate = binding.editTextLicensePlate.getText().toString();
        try {
            if (binding.radioButtonCar.isChecked()) {
                Vehicle vehicle = new Car(licensePlate, LocalDateTime.now());
                parkingViewModel.saveVehicle(vehicle).observe(this, result ->
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show());
                clearFields();
            } else if (binding.radioButtonMotorcycle.isChecked()) {
                Vehicle vehicle = new Motorcycle(licensePlate, LocalDateTime.now(), cylinderCapacity);
                parkingViewModel.saveVehicle(vehicle).observe(this, result ->
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show());
                clearFields();
            } else {
                Toast.makeText(this, "Seleccione el tipo de vehiculo", Toast.LENGTH_SHORT).show();
            }
        } catch (GlobalException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        vehicleAdapter.notifyDataSetChanged();
    }

    private void clearFields() {
        binding.editTextCylinderCapacity.setText("");
        binding.editTextLicensePlate.setText("");
    }

    public void collectParkingService(Vehicle vehicle) {
        parkingViewModel.collectParkingService(vehicle).observe(this, billParkingService -> {
            Toast.makeText(this, "Total a pagar: " + billParkingService, Toast.LENGTH_SHORT).show();
            vehicleAdapter.notifyDataSetChanged();
        });
    }
}
