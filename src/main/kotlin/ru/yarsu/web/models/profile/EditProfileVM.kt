package ru.yarsu.web.models.profile

import org.http4k.lens.MultipartForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle
import ru.yarsu.web.domain.article.Profile
import kotlin.enums.EnumEntries

class EditProfileVM(
    val profile: Profile,
    val allInstruments: EnumEntries<Instrument>,
    val allStyles: EnumEntries<MusicStyle>,
    val form: MultipartForm,
) : ViewModel {
    private val selectedStyleNames = profile.styles.map { it.name }.toSet()
    private val selectedInstrumentNames = profile.instruments.map { it.name }.toSet()
    val styleSelected: Map<String, Boolean> = allStyles.associate { it.name to (selectedStyleNames.contains(it.name)
        ?: false) }
    val instrumentSelected: Map<String, Boolean> =
        allInstruments.associate { it.name to (selectedInstrumentNames.contains(it.name) ?: false) }
}