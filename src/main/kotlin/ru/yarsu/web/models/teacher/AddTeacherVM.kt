package ru.yarsu.web.models.teacher

import org.http4k.lens.MultipartForm
import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Teacher

class AddTeacherVM(val teacher: Teacher, val form: MultipartForm) : ViewModel