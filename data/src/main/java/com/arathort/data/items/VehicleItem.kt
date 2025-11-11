package com.arathort.data.items

import com.arathort.common.comments.Comment
import com.arathort.core.entity.Vehicle
import com.arathort.data.rent.Rentable
import com.google.firebase.database.Exclude
import java.time.LocalDate
import kotlin.math.round

abstract class VehicleItem(
    var id: String = "",
    private val vehicle: Vehicle,
    open val uploadDate: String = LocalDate.now().toString(),
    private var rented: Boolean = false,
    private var comments: MutableList<Comment> = mutableListOf(),
    open var pledge: Double = 0.0,
    price: Double = 0.0
) : Rentable {

    open var price: Double = price
        set(value) {
            if (value < 0) throw IllegalArgumentException("Price can't be negative")
            field = value
        }

    fun getVehicle(): Vehicle = vehicle

    override fun calculateRentalCost(days: Int): Double {
        return price * days
    }

    override fun setRented(rented: Boolean) {
        this.rented = rented
    }

    @Exclude
    override fun isAvailable(): Boolean = !rented

    fun addComment(comment: Comment) {
        comments.add(comment)
    }

    fun getComments(): List<Comment> = comments

    fun getRating(): Double {
        if (comments.isEmpty()) return 0.0
        val sum = comments.sumOf { it.rating }
        return round(sum / comments.size.toDouble() * 10) / 10
    }

    fun isRented(): Boolean = rented
}
