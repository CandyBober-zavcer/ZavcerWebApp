package ru.yarsu.web.models.studio

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Studio

class StudioVM(
    val studio: Studio,
) : ViewModel