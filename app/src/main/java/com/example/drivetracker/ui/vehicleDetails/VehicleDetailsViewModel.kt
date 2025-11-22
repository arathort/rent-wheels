package com.example.drivetracker.ui.vehicleDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.arathort.data.items.VehicleItem
import com.arathort.data.records.CarRentalRecord
import com.arathort.data.records.TruckRentalRecord
import com.arathort.data.Payment
import com.arathort.data.repositories.PaymentRepository
import com.arathort.data.repositories.RentalRepository
import com.arathort.data.repositories.UserRepository
import com.arathort.data.repositories.VehicleRepository
import com.arathort.data.user.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class VehicleDetailsViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository,
    private val rentalRepository: RentalRepository,
    private val paymentRepository: PaymentRepository,
    auth: FirebaseAuth
) : ViewModel() {
    private val _displayedItem = MutableStateFlow<VehicleItem?>(null)
    val displayedItem: StateFlow<VehicleItem?> = _displayedItem

    private var currentUser: User? = null

    init {
        val email = auth.currentUser?.email ?: ""
        viewModelScope.launch {
            currentUser = userRepository.getUserByEmail(email) ?: User(
                email = email,
                role = if (email == "1@gmail.com") "admin" else "renter"
            )
            if (currentUser?.id.isNullOrEmpty()) {
                val id = userRepository.addUser(currentUser!!)
                currentUser?.id = id
            }
        }
    }

    fun loadVehicleById(id: String) {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getVehicleById(id)
            _displayedItem.value = vehicle
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            _displayedItem.value?.let {
                vehicleRepository.deleteVehicle(it)
            }
        }
    }

    fun updatePrice(newPrice: Double) {
        viewModelScope.launch {
            _displayedItem.value?.let {
                it.price = newPrice
                vehicleRepository.updateVehicle(it)
            }
        }
    }

    fun createAndAddRental(endDate: LocalDate): Boolean {
        val item = _displayedItem.value ?: return false
        if (!item.isAvailable()) return false
        val startDate = LocalDate.now()
        val renter = currentUser ?: return false

        val record = when (item) {
            is CarItem -> CarRentalRecord(
                item = item,
                renter = renter,
                startRentDate = startDate.toString(),
                endRentDate = endDate.toString()
            )
            is TruckItem -> TruckRentalRecord(
                item = item,
                renter = renter,
                startRentDate = startDate.toString(),
                endRentDate = endDate.toString()
            )
            else -> return false
        }

        val totalCost = record.calculateTotalCost()
        val payment = Payment(rental = record, amount = totalCost)
        if (!payment.processPayment()) return false

        item.setRented(true)
        vehicleRepository.updateVehicle(item)

        val recordId = rentalRepository.addRental(record)
        record.id = recordId
        paymentRepository.addPayment(payment)

        renter.addRental(record)
        return true
    }

    fun isAdmin(): Boolean = currentUser?.role == "admin"
}
