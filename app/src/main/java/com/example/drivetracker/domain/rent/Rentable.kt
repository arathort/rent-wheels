package com.example.drivetracker.domain.rent

interface Rentable {
    fun calculateRentalCost(days: Int): Double
    fun isAvailable(): Boolean
    fun setRented(rented: Boolean)
}