package ru.yarsu.web.models.auth

import org.http4k.template.ViewModel

data class ResetPasswordVM(val token: String) : ViewModel {
    override fun template(): String = "ru/yarsu/web/models/auth/ResetPasswordVM"
}
