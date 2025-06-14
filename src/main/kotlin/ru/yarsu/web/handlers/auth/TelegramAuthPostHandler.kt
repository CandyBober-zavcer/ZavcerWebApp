package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.models.telegram.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TelegramAuthPostHandler(
    private val jsonLogger: JsonLogger,
    private val botToken: String,
    private val databaseController: DatabaseController,
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

            val user = databaseController.findOrCreateTelegramUser(telegramUser)

            Response(Status.FOUND)
                .header("Location", "/")
                .cookie(createAuthCookie(user, authSalt))
        } catch (e: Exception) {
            e.printStackTrace()
            Response(Status.BAD_REQUEST).body("Ошибка авторизации через Telegram")
        }

    private fun createAuthCookie(
        user: User,
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
