package ru.yarsu.web.models.admin

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

class UserVM(
    val user: UserModel,
    val hasTeacherRole: Boolean,
    val hasOwnerRole: Boolean,
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
}
