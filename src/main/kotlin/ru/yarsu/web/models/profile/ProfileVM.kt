package ru.yarsu.web.models.profile

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User

class ProfileVM(
    val user: User,
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
