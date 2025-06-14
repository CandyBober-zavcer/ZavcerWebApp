package ru.yarsu.web.models.admin

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.RoleEnums

class UserVM(
    val user: User,
    val hasTeacherRole: Boolean,
    val hasOwnerRole: Boolean,
    private val sessionUser: User
) : ViewModel {
    val abilityNames: String =
        user.abilities
            .mapIndexed { index, ability ->
                if (index == 0) {
                    ability.instrument.replaceFirstChar { it.uppercase() }
                } else {
                    ability.instrument
                }
            }.joinToString(", ")

    val isAdmin: Boolean = sessionUser.roles.contains(RoleEnums.ADMIN)
}
