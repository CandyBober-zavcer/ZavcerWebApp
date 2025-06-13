package ru.yarsu.web.models.admin

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

class UserListVM(val users: List<UserModel>) : ViewModel