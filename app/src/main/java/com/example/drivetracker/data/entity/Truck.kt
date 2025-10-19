package com.example.drivetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

class Truck(
    override val brand: String = "",
    override val model: String = "",
    override val registrationNumber: String = "",
    override val year: Int = 0,
    val cargoCapacity: Double = 0.0
) : Vehicle(brand, model, year, registrationNumber)