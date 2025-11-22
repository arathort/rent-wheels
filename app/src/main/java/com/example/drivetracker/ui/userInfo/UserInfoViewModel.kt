package com.example.drivetracker.ui.userInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arathort.data.user.User
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.arathort.data.records.CarRentalRecord
import com.arathort.data.records.TruckRentalRecord
import com.arathort.data.repositories.RentalRepository
import com.arathort.data.repositories.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val rentalRepository: RentalRepository,
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
            val records = rentalRepository.getRentalsByUser(
                user = User(email = userEmail),
                type = "Cars"
            ).filterIsInstance<CarRentalRecord>()
            _carRecords.value = records
        }
    }

    fun loadTruckRecords() {
        viewModelScope.launch {
            val userEmail = auth.currentUser?.email ?: return@launch
            val records = rentalRepository.getRentalsByUser(
                user = User(email = userEmail),
                type = "Trucks"
            ).filterIsInstance<TruckRentalRecord>()
            _truckRecords.value = records
        }
    }

    fun updateCar(car: CarItem) {
        vehicleRepository.updateVehicle(car)
    }

    fun updateTruck(truckItem: TruckItem) {
        vehicleRepository.updateVehicle(truckItem)
    }

    fun updateCarRecord(carRecord: CarRentalRecord) {
        rentalRepository.updateRentalRecord(carRecord)
    }

    fun updateTruckRecord(truckRecord: TruckRentalRecord) {
        rentalRepository.updateRentalRecord(truckRecord)
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
