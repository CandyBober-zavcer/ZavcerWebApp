package ru.yarsu.web.models.teacher

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

class TeachersVM(val teachers: List<UserModel>) : ViewModel