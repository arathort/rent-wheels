package com.example.drivetracker.data.comments

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Comment(
    val authorEmail: String = "",
    val text: String = "",
    val rating: Int = 0,
    val writingDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
) {
    init {
        if (rating !in 1..5) throw IllegalArgumentException("Rating must be 1-5")
    }
}