package com.example.drivetracker.data.records

import com.example.drivetracker.data.items.TruckItem
import com.example.drivetracker.domain.rent.RentalRecord
import com.example.drivetracker.domain.user.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TruckRentalRecord(
    override val item: TruckItem = TruckItem(),
    renter: User,
    startRentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    endRentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
) : RentalRecord(
    item = item,
    renter = renter,
    startRentDate = startRentDate,
    endRentDate = endRentDate
) {
    override fun calculateTotalCost(): Double {
        return item.calculateRentalCost(getDuration().toInt()) + item.pledge * 1.1
    }
}