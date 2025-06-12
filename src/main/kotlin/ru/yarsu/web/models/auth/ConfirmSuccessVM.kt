package ru.yarsu.web.models.auth

import org.http4k.template.ViewModel

data class ConfirmSuccessVM(
    val redirectUrl: String,
) : ViewModel {
    override fun template(): String = "ru/yarsu/web/models/auth/ConfirmSuccessVM"
}
