package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.models.telegram.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TelegramAuthPostHandler(
    private val jsonLogger: JsonLogger,
    private val botToken: String,
    private val users: UserData,
    private val authSalt: String,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        try {
            val body = request.bodyString()
            val telegramData = jsonLogger.parseTelegramData(body, botToken)

            val telegramUser =
                TelegramUser(
                    id = telegramData.id,
                    username = telegramData.username,
                    first_name = telegramData.firstName,
                    last_name = telegramData.lastName,
                )

            val user = users.findOrCreateTelegramUser(telegramUser)

            Response(Status.FOUND)
                .header("Location", "/")
                .cookie(createAuthCookie(user, authSalt))
        } catch (e: Exception) {
            e.printStackTrace()
            Response(Status.BAD_REQUEST).body("Ошибка авторизации через Telegram")
        }

    private fun createAuthCookie(
        user: UserModel,
        authSalt: String,
    ): Cookie {
        val rawData = "${user.id}:${user.login}"
        val signature = AuthUtils.hmacSign(rawData, authSalt)

        return Cookie(
            name = "auth",
            value = "$rawData:$signature",
            path = "/",
            httpOnly = true,
            secure = true,
            sameSite = SameSite.Strict,
            expires =
                ZonedDateTime
                    .of(
                        LocalDateTime.now().plusDays(7),
                        ZoneId.of("Europe/Moscow"),
                    ).toInstant(),
        )
    }
}
