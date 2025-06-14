package ru.yarsu.web.domain.classes

import ru.yarsu.web.domain.classes.`interface`.PaidPlace
import ru.yarsu.web.domain.enums.DistrictEnums

data class Spot(
    override var id: Int = -1,
    override var name: String = "",
    override var price: Int = 0,
    var hasDrums: Boolean = false,
    var guitarAmps: Int = 0,
    var bassAmps: Int = 0,
    override var description: String = "",
    override var address: String = "",
    override var district: DistrictEnums = DistrictEnums.UNKNOWN,
    override var images: List<String> = listOf(),
    override var twoWeekOccupation: MutableList<Int> = mutableListOf(),
    var owners: List<Int> = mutableListOf()
) : PaidPlace
