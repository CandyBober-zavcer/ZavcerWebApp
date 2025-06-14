package ru.yarsu.web.models.upgrade

import org.http4k.lens.MultipartForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import kotlin.enums.EnumEntries

class UpgradeUserToOwnerVM(
    val user: User,
    private val allAbility: EnumEntries<AbilityEnums>,
    val form: MultipartForm,
) : ViewModel {
    private val selectedAbilityNames: Set<String> = user.abilities.map { it.name }.toSet()

    val abilitySelected: Map<String, Boolean> =
        allAbility.associate { abilityEnum ->
            abilityEnum.name to selectedAbilityNames.contains(abilityEnum.name)
        }

    val allAbilityWithNames: List<Pair<String, String>> =
        allAbility.map {
            it.name to it.instrument.replaceFirstChar { ch -> ch.uppercaseChar() }
        }

    val allDistricts = DistrictEnums.entries
}
