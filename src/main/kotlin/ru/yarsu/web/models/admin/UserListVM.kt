package ru.yarsu.web.models.admin

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User

class UserListVM(
    val users: List<User>,
) : ViewModel