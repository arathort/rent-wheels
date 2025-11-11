package com.arathort.data.items

import com.arathort.core.entity.Truck
import com.arathort.data.rent.Rentable

class TruckItem(
    val truck: Truck = Truck(),
    override val uploadDate: String = "",
    override var pledge: Double = 0.0,
    price: Double = 0.0
) : VehicleItem(vehicle = truck, uploadDate = uploadDate, pledge = pledge, price = price),
    Rentable {

    fun validateCargo(capacityNeeded: Double): Boolean {
        return truck.cargoCapacity >= capacityNeeded
    }

    override fun calculateRentalCost(days: Int): Double {
        return super.calculateRentalCost(days) + (truck.cargoCapacity * 0.1)
    }
}
