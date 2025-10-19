package com.example.drivetracker.model

import com.example.drivetracker.data.items.CarItem
import com.example.drivetracker.data.items.TruckItem

data class OrderVehicleUiState(
    val isTruck: Boolean = false,
    val cars: List<CarItem> = emptyList(),
    val trucks: List<TruckItem> = emptyList(),
    val navigateTo: String? = null
)
