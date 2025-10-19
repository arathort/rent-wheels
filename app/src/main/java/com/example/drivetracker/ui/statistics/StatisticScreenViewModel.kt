package com.example.drivetracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivetracker.data.VehicleRepository
import com.example.drivetracker.data.items.CarItem
import com.example.drivetracker.data.items.TruckItem
import com.example.drivetracker.data.records.CarRentalRecord
import com.example.drivetracker.data.records.TruckRentalRecord
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatisticScreenViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _cars = MutableStateFlow<List<CarItem>>(emptyList())
    val cars: StateFlow<List<CarItem>> = _cars

    private val _trucks = MutableStateFlow<List<TruckItem>>(emptyList())
    val trucks: StateFlow<List<TruckItem>> = _trucks

    private val _carRecords = MutableStateFlow<List<CarRentalRecord>>(emptyList())
    val carRecords: StateFlow<List<CarRentalRecord>> = _carRecords

    private val _truckRecords = MutableStateFlow<List<TruckRentalRecord>>(emptyList())
    val truckRecords: StateFlow<List<TruckRentalRecord>> = _truckRecords

    init {
        loadCars()
        loadTrucks()
        loadCarRecords()
        loadTruckRecords()
    }

    private fun loadCars() {
        viewModelScope.launch {
            val list = vehicleRepository.getVehicleItems("Cars")
                .filterIsInstance<CarItem>()
            _cars.value = list
        }
    }

    private fun loadTrucks() {
        viewModelScope.launch {
            val list = vehicleRepository.getVehicleItems("Trucks")
                .filterIsInstance<TruckItem>()
            _trucks.value = list
        }
    }

    private fun loadCarRecords() {
        viewModelScope.launch {
            val records = vehicleRepository.getRentalRecords("Cars")
                .filterIsInstance<CarRentalRecord>()
            _carRecords.value = records
        }
    }

    private fun loadTruckRecords() {
        viewModelScope.launch {
            val records = vehicleRepository.getRentalRecords("Trucks")
                .filterIsInstance<TruckRentalRecord>()
            _truckRecords.value = records
        }
    }

    fun getNumberOfRent(carItem: CarItem): Int =
        _carRecords.value.count { it.item.car == carItem.car }

    fun getNumberOfRent(truckItem: TruckItem): Int =
        _truckRecords.value.count { it.item.truck == truckItem.truck }

    fun getCarOwner(carItem: CarItem): String =
        _carRecords.value.firstOrNull { it.item.car == carItem.car && it.isActive }?.renter?.email.orEmpty()

    fun getTruckOwner(truckItem: TruckItem): String =
        _truckRecords.value.firstOrNull { it.item.truck == truckItem.truck && it.isActive }?.renter?.email.orEmpty()

    fun isAdmin(): Boolean =
        auth.currentUser?.email == "1@gmail.com"

    fun exit() {
        auth.signOut()
    }
}
