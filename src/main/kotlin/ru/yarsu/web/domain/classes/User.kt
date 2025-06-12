package ru.yarsu.web.domain.classes

import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.RoleEnums

data class User(
    var id: Int = -1,
    var name: String = "",
    var tg_id: Long = 0L,
    var login: String = "",
    var password: String = "",
    var phone: String = "+7 (4852) 73-88-15",
    var experience: Int = 0,
    var abilities: MutableSet<AbilityEnums> = mutableSetOf(),
    var price: Int = 0,
    var description: String = "",
    var address: String = "Ярославль, ул. Загородный Сад, 6",
    var district: DistrictEnums = DistrictEnums.UNKNOWN,
    var images: List<String> = listOf(),
    var twoWeekOccupation: List<Int> = listOf(),
    var spots: List<Int> = listOf(),
    var roles: MutableSet<RoleEnums> = mutableSetOf(RoleEnums.ANONYMOUS),
    var isConfirmed: Boolean = true,
)
