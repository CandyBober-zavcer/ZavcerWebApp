package ru.yarsu.web.domain.classes.`interface`

import ru.yarsu.web.domain.enums.DistrictEnums

interface PaidPlace {
    val id: Int
    var name: String
    var price: Int
    var description: String
    var address: String
    var district: DistrictEnums
    var images: List<String>
    var twoWeekOccupation: MutableList<Int>
}
