package ru.yarsu.web.models.spot

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.Paginator
import ru.yarsu.web.domain.classes.Spot
import ru.yarsu.web.domain.enums.DistrictEnums

data class SpotsVM(
    val spots: List<Spot>,
    val districtEnums: List<DistrictEnums>,
) : ViewModel
