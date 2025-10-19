package com.example.drivetracker.domain.user

import com.example.drivetracker.domain.rent.RentalRecord

class User(
    var id: String = "",
    val email: String = "",
    val role: String = "renter",
    val rentalHistory: MutableList<RentalRecord> = mutableListOf()
) {
    fun addRental(record: RentalRecord) {
        rentalHistory.add(record)
    }
}