package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle

data class Profile(
    val id: Long,
    val name: String?,
    val description: String?,
    val instruments: List<Instrument>?,
    val styles: List<MusicStyle>?,
    val avatarUrl: String?,
)
