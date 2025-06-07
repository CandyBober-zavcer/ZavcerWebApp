package ru.yarsu.web.models.teacher

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

class DeleteTeacherVM(
    val teacher: UserModel,
) : ViewModel
