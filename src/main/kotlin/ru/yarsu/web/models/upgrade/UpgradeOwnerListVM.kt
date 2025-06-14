package ru.yarsu.web.models.upgrade

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.classes.User

class UpgradeOwnerListVM(
    val users: List<User>,
) : ViewModel
