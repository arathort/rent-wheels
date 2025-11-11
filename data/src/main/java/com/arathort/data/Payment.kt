package com.arathort.data

import com.arathort.data.rent.RentalRecord

class Payment(
    var id: String = "",
    val rental: RentalRecord,
    val amount: Double,
    val paid: Boolean = false
) {
    fun processPayment(): Boolean {
        if (amount <= 0) return false
        return true
    }

    fun refundPledge(): Double {
        if (!rental.isActive) return rental.item.pledge
        return 0.0
    }
}