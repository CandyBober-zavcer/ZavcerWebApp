package ru.yarsu.web.models.upgrade

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

data class UpgradeOwnerProfileVM(
    val user: UserModel,
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
