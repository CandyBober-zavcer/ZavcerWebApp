package ru.yarsu.web.models.teacher

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.ExpEnums
import ru.yarsu.web.handlers.teacher.TechersWebParams

class TeachersVM(
    val teachers: List<User>,
    val expEnums: List<ExpEnums>,
    val abilityEnums: List<AbilityEnums>,
    val districtEnums: List<DistrictEnums>,
    val form: TechersWebParams,
) : ViewModel
