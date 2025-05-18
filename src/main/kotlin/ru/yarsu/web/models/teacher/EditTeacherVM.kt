package ru.yarsu.web.models.teacher

import org.http4k.lens.MultipartForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle
import ru.yarsu.web.domain.article.Teacher
import kotlin.enums.EnumEntries

class EditTeacherVM(
    val teacher: Teacher,
    val user: String,
    val allStyles: EnumEntries<MusicStyle>,
    val allInstruments: EnumEntries<Instrument>,
    val form: MultipartForm,
) : ViewModel {
    private val selectedStyleNames = teacher.experienceInfo.styles.map { it.name }.toSet()
    private val selectedInstrumentNames = teacher.instruments.map { it.name }.toSet()
    val styleSelected: Map<String, Boolean> = allStyles.associate { it.name to selectedStyleNames.contains(it.name) }
    val instrumentSelected: Map<String, Boolean> =
        allInstruments.associate { it.name to selectedInstrumentNames.contains(it.name) }
}