package ru.yarsu.web.models.upgrade

import org.http4k.template.ViewModel
import ru.yarsu.web.domain.article.UserModel

class UpgradeListVM(val users: List<UserModel>) : ViewModel