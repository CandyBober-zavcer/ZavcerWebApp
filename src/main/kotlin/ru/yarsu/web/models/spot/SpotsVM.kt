package ru.yarsu.web.models.spot

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Spot

data class SpotsVM(
    val spots: List<Spot>,
) : ViewModel
