package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.enums.*

data class UserModel(
    var id: Int = -1,
    var name: String = "",
    var tg_id: Long = 0L,
    var login: String = "",
    var password: String = "",
    var phone: String = "8 800 555 35 35",
    var experience: Int = 0,
    var abilities: Set<AbilityEnums> = emptySet(),
    var price: Int = 0,
    var description: String = "",
    var address: String = "",
    var district: DistrictEnums = DistrictEnums.UNKNOWN,
    var images: List<String> = listOf(),
    var twoWeekOccupation: List<Int> = listOf(),
    var spots: List<Int> = listOf(),
    var roles: Set<RoleEnums> = setOf(RoleEnums.ANONYMOUS),
    var isConfirmed: Boolean = true,
)
