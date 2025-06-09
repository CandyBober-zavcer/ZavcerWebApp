package ru.yarsu.web.models.auth

import org.http4k.template.ViewModel

class ResetForgotPasswordGetVM : ViewModel {
    override fun template(): String = "ru/yarsu/web/models/auth/ResetForgotPasswordGetVM"
}
