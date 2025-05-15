package ru.yarsu.web.models.teacher

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.Teacher

class EditTeacherVM(val teacher: Teacher,
                    val user: String
) : ViewModel