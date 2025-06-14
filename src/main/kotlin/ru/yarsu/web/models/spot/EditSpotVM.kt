package ru.yarsu.web.models.spot

import org.http4k.lens.MultipartForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.Spot
import ru.yarsu.web.domain.enums.DistrictEnums

data class EditSpotVM(
    val spot: Spot,
    val form: MultipartForm,
) : ViewModel {
    val allDistricts = DistrictEnums.entries
}
