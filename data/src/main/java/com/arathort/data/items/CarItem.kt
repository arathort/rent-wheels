package com.arathort.data.items

import com.arathort.core.entity.Car
import com.arathort.data.rent.Rentable

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
