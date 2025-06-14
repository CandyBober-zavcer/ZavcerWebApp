package ru.yarsu.web.domain.classes

import ru.yarsu.web.domain.classes.`interface`.PaidPlace
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.RoleEnums

data class User(
    override var id: Int = -1,
    override var name: String = "",
    var tg_id: Long = 0L,
    var login: String = "",
    var password: String = "",
    var phone: String = "+7 (4852) 73-88-15",
    var experience: Int = 0,
    var abilities: MutableSet<AbilityEnums> = mutableSetOf(),
    override var price: Int = 0,
    override var description: String = "",
    override var address: String = "Ярославль, ул. Загородный Сад, 6",
    override var district: DistrictEnums = DistrictEnums.UNKNOWN,
    override var images: List<String> = listOf(),
    override var twoWeekOccupation: MutableList<Int> = mutableListOf(),
    var spots: List<Int> = listOf(),
    var roles: MutableSet<RoleEnums> = mutableSetOf(RoleEnums.ANONYMOUS),
    var isConfirmed: Boolean = true
) : PaidPlace
