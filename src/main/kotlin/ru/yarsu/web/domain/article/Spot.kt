package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.enums.DistrictEnums

data class Spot(
    var id: Int = -1,
    var name: String = "",
    var price: Int = 0,
    var hasDrums: Boolean = false,
    var guitarAmps: Int = 0,
    var bassAmps: Int = 0,
    var description: String = "",
    var address: String = "",
    var district: DistrictEnums = DistrictEnums.UNKNOWN,
    var images: List<String> = mutableListOf(),
    var twoWeekOccupation: MutableList<Int> = mutableListOf(),
    var owners: List<Int> = mutableListOf(),
)
