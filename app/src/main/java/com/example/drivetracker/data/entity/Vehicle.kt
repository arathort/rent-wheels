package com.example.drivetracker.data.entity

open class Vehicle(
    open val brand: String = "",
    open val model: String = "",
    open val year: Int = 0,
    open val registrationNumber: String = ""
)