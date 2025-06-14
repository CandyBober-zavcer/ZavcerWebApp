package ru.yarsu.web.models.spot

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.Spot

data class SpotVM(val spot: Spot, val freeSlots: String, val phone: String?) : ViewModel
