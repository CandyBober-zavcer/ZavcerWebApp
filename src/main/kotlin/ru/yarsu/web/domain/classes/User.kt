package ru.yarsu.web.domain.classes

import ru.yarsu.web.domain.RoleEnums

data class User(
    var id: Int = -1,
    var name: String = "",
    var tg_name: String = "",
    var password: String = "",
    var description: String = "",
    var images: List<String> = listOf(),
    var twoWeekOccupation: List<Int> = listOf(),
    var spots: List<Int> = listOf(),
    var roles: MutableSet<RoleEnums> = mutableSetOf(RoleEnums.ANONYMOUS),
)
