package ru.yarsu.web.models.spot

import ru.yarsu.web.domain.article.Spot
import org.http4k.template.ViewModel

data class SpotVM(val spot: Spot) : ViewModel
