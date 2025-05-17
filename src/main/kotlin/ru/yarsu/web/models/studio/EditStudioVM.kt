package ru.yarsu.web.models.studio

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.Studio
import kotlin.enums.EnumEntries

class EditStudioVM(
    val studio: Studio,
    val user: String,
    val allInstruments: EnumEntries<Instrument>
) : ViewModel {
    private val selectedInstrumentNames = studio.equipment.map { it.name }.toSet()
    val instrumentSelected: Map<String, Boolean> =
        allInstruments.associate { it.name to selectedInstrumentNames.contains(it.name) }
}