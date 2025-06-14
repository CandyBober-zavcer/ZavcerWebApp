package ru.yarsu.web.models.teacher

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User

class TeachersVM(
    val teachers: List<User>,
) : ViewModel
