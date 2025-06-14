package ru.yarsu.web.domain.classes

data class DayOccupation(
    var id: Int = -1,
    var occupation: MutableMap<Int, Int?> = mutableMapOf(),
)
