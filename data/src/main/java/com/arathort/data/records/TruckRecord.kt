package com.arathort.data.records

import com.arathort.data.rent.RentalRecord
import com.arathort.data.user.User
import com.arathort.data.items.TruckItem
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