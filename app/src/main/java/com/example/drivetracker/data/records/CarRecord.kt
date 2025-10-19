package com.example.drivetracker.data.records

import com.example.drivetracker.data.items.CarItem
import com.example.drivetracker.domain.rent.RentalRecord
import com.example.drivetracker.domain.user.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CarRentalRecord(
    override val item: CarItem = CarItem(),
    renter: User,
    startRentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    endRentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
) : RentalRecord(item = item, renter = renter, startRentDate = startRentDate, endRentDate =  endRentDate) {
    override fun calculateTotalCost(): Double {
        return item.calculateRentalCost(getDuration().toInt()) + item.pledge
    }
}