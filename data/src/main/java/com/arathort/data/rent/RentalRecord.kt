package com.arathort.data.rent

import com.arathort.data.user.User
import com.arathort.data.items.VehicleItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

abstract class RentalRecord(
    open val item: VehicleItem,
    var id: String = "",
    val renter: User,
    val startRentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val endRentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    var isActive: Boolean = true
) {
    init {
        item.setRented(true)
    }

    fun setPassive() {
        isActive = false
        item.setRented(false)
    }

    fun getDuration(): Long {
        val start = LocalDate.parse(startRentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val end = LocalDate.parse(endRentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ChronoUnit.DAYS.between(start, end)
    }

    abstract fun calculateTotalCost(): Double
}