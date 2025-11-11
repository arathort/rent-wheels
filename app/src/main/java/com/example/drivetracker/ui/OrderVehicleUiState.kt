package com.example.drivetracker.ui

import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem

data class OrderVehicleUiState(
    val isTruck: Boolean = false,
    val cars: List<CarItem> = emptyList(),
    val trucks: List<TruckItem> = emptyList(),
    val navigateTo: String? = null
)