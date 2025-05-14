package ru.yarsu.web.domain.classes

data class Spot(
    var id: Int = -1,
    var name: String = "",
    var description: String = "",
    var images: List<String> = mutableListOf(),
    var twoWeekOccupation: MutableList<Int> = mutableListOf(),
    var owners: List<Int> = mutableListOf(),
)
