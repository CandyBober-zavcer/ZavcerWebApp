package ru.yarsu.web.models.profile

import org.http4k.lens.MultipartForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.article.UserModel
import kotlin.enums.EnumEntries

class EditProfileVM(
    val user: UserModel,
    private val allAbility: EnumEntries<AbilityEnums>,
    val form: MultipartForm,
) : ViewModel {
    private val selectedAbilityNames: Set<String> = user.abilities.map { it.name }.toSet()

    val abilitySelected: Map<String, Boolean> = allAbility.associate { abilityEnum ->
        abilityEnum.name to selectedAbilityNames.contains(abilityEnum.name)
    }

    val allAbilityWithNames: List<Pair<String, String>> = allAbility.map {
        it.name to it.instrument.replaceFirstChar { ch -> ch.uppercaseChar() }
    }
}
