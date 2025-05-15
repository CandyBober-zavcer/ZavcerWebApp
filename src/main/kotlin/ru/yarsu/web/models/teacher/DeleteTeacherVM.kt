package ru.yarsu.web.models.teacher

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Teacher

class DeleteTeacherVM(val teachers: List<Teacher>) : ViewModel