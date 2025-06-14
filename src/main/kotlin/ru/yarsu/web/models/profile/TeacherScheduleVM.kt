package ru.yarsu.web.models.profile

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User

class TeacherScheduleVM(
    val user: User,
    val freeSlotsTeacher: String,
    val blockedSlotsTeacher: String,
    val freeSlotsOwner: String,
    val blockedSlotsOwner: String
) : ViewModel {
}