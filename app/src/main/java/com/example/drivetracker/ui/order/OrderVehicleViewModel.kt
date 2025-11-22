package com.example.drivetracker.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.arathort.data.items.VehicleItem
import com.arathort.data.repositories.UserRepository
import com.arathort.data.repositories.VehicleRepository
import com.arathort.data.user.User
import com.example.drivetracker.ui.OrderVehicleUiState
import com.example.drivetracker.ui.RentWheelsScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderVehicleUiState())
    val uiState: StateFlow<OrderVehicleUiState> = _uiState.asStateFlow()

    private var currentUser: User? = null

    init {
        viewModelScope.launch {
            val email = auth.currentUser?.email ?: ""
            currentUser = userRepository.getUserByEmail(email) ?: User(
                email = email,
                role = if (email == "1@gmail.com") "admin" else "renter"
            )
            if (currentUser?.id.isNullOrEmpty()) {
                val id = userRepository.addUser(currentUser!!)
                currentUser?.id = id.toString()
            }
            fetchVehicles()
        }
    }

    fun selectVehicle(vehicle: VehicleItem) {
        vehicleRepository.setSelectedVehicle(vehicle)
    }

    private suspend fun fetchVehicles() {
        val cars = vehicleRepository.getVehicleItems("Cars")
        val trucks = vehicleRepository.getVehicleItems("Trucks")
        _uiState.update {
            it.copy(
                cars = cars as List<CarItem>,
                trucks = trucks as List<TruckItem>
            )
        }
    }

    fun changeVehicleType(isTruck: Boolean) {
        _uiState.update { it.copy(isTruck = isTruck) }
    }

    fun getEnableVehicles(): List<VehicleItem> {
        return _uiState.value.let { state ->
            val vehicles = if (state.isTruck) state.trucks else state.cars
            vehicles.filter { !it.isRented() }
        }
    }

    fun navigateToAddScreen(vehicleType: String) {
        _uiState.update {
            it.copy(
                navigateTo = when (vehicleType) {
                    "Car" -> RentWheelsScreen.AddCar.name
                    "Truck" -> RentWheelsScreen.AddTruck.name
                    else -> null
                }
            )
        }
    }

    fun onNavigateDone() {
        _uiState.update { it.copy(navigateTo = null) }
    }

    fun isAdmin(): Boolean {
        return currentUser?.role == "admin"
    }

    fun addVehicleItem(vehicleItem: VehicleItem) {
        vehicleRepository.addVehicle(vehicleItem)
    }
}