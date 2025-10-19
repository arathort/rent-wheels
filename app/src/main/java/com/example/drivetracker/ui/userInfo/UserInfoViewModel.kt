package com.example.drivetracker.ui.userInfo

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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UserInfoViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _carRecords = MutableStateFlow<List<CarRentalRecord>>(emptyList())
    val carRecords: StateFlow<List<CarRentalRecord>> get() = _carRecords

    private val _truckRecords = MutableStateFlow<List<TruckRentalRecord>>(emptyList())
    val truckRecords: StateFlow<List<TruckRentalRecord>> get() = _truckRecords

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun loadCarRecords() {
        viewModelScope.launch {
            val userEmail = auth.currentUser?.email ?: return@launch
            val records = vehicleRepository.getRentalRecordsByUser(
                user = com.example.drivetracker.domain.user.User(email = userEmail),
                type = "Cars"
            ).filterIsInstance<CarRentalRecord>()
            _carRecords.value = records
        }
    }

    fun loadTruckRecords() {
        viewModelScope.launch {
            val userEmail = auth.currentUser?.email ?: return@launch
            val records = vehicleRepository.getRentalRecordsByUser(
                user = com.example.drivetracker.domain.user.User(email = userEmail),
                type = "Trucks"
            ).filterIsInstance<TruckRentalRecord>()
            _truckRecords.value = records
        }
    }

    fun updateCar(car: CarItem) {
        vehicleRepository.updateVehicleItem(car)
    }

    fun updateTruck(truckItem: TruckItem) {
        vehicleRepository.updateVehicleItem(truckItem)
    }

    fun updateCarRecord(carRecord: CarRentalRecord) {
        vehicleRepository.updateRentalRecord(carRecord)
    }

    fun updateTruckRecord(truckRecord: TruckRentalRecord) {
        vehicleRepository.updateRentalRecord(truckRecord)
    }

    fun isAdmin(): Boolean {
        return auth.currentUser?.email == "1@gmail.com"
    }

    fun isDateExpired(endRentDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.now().isAfter(LocalDate.parse(endRentDate, formatter))
    }

    fun exit() {
        auth.signOut()
    }
}
