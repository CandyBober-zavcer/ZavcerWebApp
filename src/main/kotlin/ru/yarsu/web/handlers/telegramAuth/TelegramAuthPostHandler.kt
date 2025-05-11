package ru.yarsu.web.handlers.telegramAuth

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.format.Jackson
import ru.yarsu.web.domain.models.telegram.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TelegramAuthPostHandler(
    private val jsonLogger: JsonLogger,
    private val botToken: String
) : HttpHandler {

    override fun invoke(request: Request): Response {
        return try {
            val formData = request.parseJsonData()
            println("Received auth data: $formData")

            if (!isValidTelegramAuth(formData, botToken)) {
                println("Telegram auth validation failed")
                return Response(Status.UNAUTHORIZED).body("Invalid Telegram auth data")
            }

            val telegramUser = TelegramUser(
                id = formData["id"]!!.toLong(),
                firstName = formData["first_name"],
                lastName = formData["last_name"],
                username = formData["username"],
                photoUrl = formData["photo_url"],
                authDate = formData["auth_date"]!!.toLong(),
                hash = formData["hash"]!!
            )

            jsonLogger.logToJson(telegramUser)

            return Response(Status.FOUND)
                .header("ru.yarsu.web.domain.article.Location", "/")
                .cookie(createAuthCookie(telegramUser))

        } catch (e: Exception) {
            println("Telegram auth error: ${e.stackTraceToString()}")
            Response(Status.BAD_REQUEST).body("Error processing Telegram auth")
        }
    }

    private fun Request.parseJsonData(): Map<String, String> {
        val json = bodyString()
        val parsed = Jackson.parse(json)
        return parsed.fields().asSequence().associate { it.key to it.value.asText() }
    }

    private fun createAuthCookie(user: TelegramUser): Cookie {
        return Cookie(
            name = "tg_auth",
            value = "${user.id}:${user.username ?: ""}",
            path = "/",
            httpOnly = true,
            secure = true,
            sameSite = SameSite.Strict,
            expires = ZonedDateTime.of(
                LocalDateTime.now().plusDays(7),
                ZoneId.of("Europe/Moscow")
            ).toInstant()
        )
    }
}
