package ru.yarsu.web.domain.models

data class BookingRequest(
    val selectedDate: String,
    val startTime: String,
    val endTime: String
)