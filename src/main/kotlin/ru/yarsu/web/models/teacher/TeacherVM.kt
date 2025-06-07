package ru.yarsu.web.models.teacher

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

class TeacherVM(
    val teacher: UserModel,
) : ViewModel {
    val abilityNames: String =
        teacher.abilities
            .mapIndexed { index, ability ->
                if (index == 0) {
                    ability.instrument.replaceFirstChar { it.uppercase() }
                } else {
                    ability.instrument
                }
            }.joinToString(", ")
}
