package com.example.drivetracker.data.items

import com.example.drivetracker.data.entity.Car
import com.example.drivetracker.domain.rent.Rentable

class CarItem(
    val car: Car = Car(),
    override val uploadDate: String = "",
    override var pledge: Double = 0.0,
    price: Double = 0.0
) : VehicleItem(vehicle = car, uploadDate = uploadDate, pledge = pledge, price = price), Rentable {

    fun validateSeats(seatsNeeded: Int): Boolean {
        return car.numberSeats >= seatsNeeded
    }
}
