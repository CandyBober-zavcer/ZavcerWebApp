package ru.yarsu.web.models.spot

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.Spot

data class DeleteSpotVM(
    val spot: Spot,
) : ViewModel
