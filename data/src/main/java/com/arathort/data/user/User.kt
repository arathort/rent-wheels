package com.arathort.data.user

import com.arathort.data.rent.RentalRecord

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