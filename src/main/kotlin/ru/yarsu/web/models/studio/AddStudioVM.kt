package ru.yarsu.web.models.studio

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Studio
data class AddStudioVM(val studios: List<Studio>) : ViewModel