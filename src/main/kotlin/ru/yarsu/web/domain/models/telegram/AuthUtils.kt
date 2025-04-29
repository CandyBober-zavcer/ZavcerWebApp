package ru.yarsu.web.domain.models.telegram

import org.http4k.core.Request
import org.http4k.core.cookie.cookies

object AuthUtils {

    fun getUserFromCookie(request: Request): TelegramUser? {
        val tgAuthCookie = request.cookies().find { it.name == "tg_auth" }
        tgAuthCookie?.let {
            val userData = it.value.split(":")
            if (userData.size == 2) {
                val userId = userData[0].toLongOrNull()
                val username = userData[1]
                if (userId != null) {
                    return TelegramUser(id = userId, username = username)
                }
            }
        }
        return null
    }
}
