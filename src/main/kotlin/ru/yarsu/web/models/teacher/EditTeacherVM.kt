package ru.yarsu.web.models.teacher

import org.http4k.lens.MultipartForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import kotlin.enums.EnumEntries

class EditTeacherVM(
    val teacher: UserModel,
    private val ability: EnumEntries<AbilityEnums>,
    val form: MultipartForm,
) : ViewModel {

    private val selectedAbilityNames: Set<String> = teacher.abilities.map { it.name }.toSet()

    val abilitySelected: Map<String, Boolean> = ability.associate { abilityEnum ->
        abilityEnum.name to selectedAbilityNames.contains(abilityEnum.name)
    }

    val allAbilityWithNames: List<Pair<String, String>> = ability.map {
        it.name to it.instrument.replaceFirstChar { ch -> ch.uppercaseChar() }
    }

    val allDistricts = DistrictEnums.entries

}