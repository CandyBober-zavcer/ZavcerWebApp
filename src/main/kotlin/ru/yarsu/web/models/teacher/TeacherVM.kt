package ru.yarsu.web.models.teacher

import org.http4k.template.ViewModel

class TeacherVM(
    val description: String,
    val user: String
) : ViewModel