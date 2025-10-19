package com.example.drivetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Car(
    override val brand: String = "",
    override val model: String = "",
    override val year: Int = 0,
    override val registrationNumber: String = "",
    val numberSeats: Int = 0,
    val maxSpeed: Double = 0.0
) : Vehicle(brand, model, year, registrationNumber)