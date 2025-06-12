package ru.yarsu.web.models.upgrade

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

class UpgradeOwnerListVM(
    val users: List<UserModel>,
) : ViewModel
