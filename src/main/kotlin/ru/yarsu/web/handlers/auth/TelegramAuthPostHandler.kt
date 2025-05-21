package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.format.Jackson
import ru.yarsu.config.AppConfig
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.models.telegram.*
import ru.yarsu.web.domain.models.telegram.AuthUtils.hmacSign
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TelegramAuthPostHandler(
    private val jsonLogger: JsonLogger,
    private val botToken: String,
    private val users: UserData
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
                first_name = formData["first_name"],
                last_name = formData["last_name"],
                username = formData["username"],
                photo_url = formData["photo_url"],
                auth_date = formData["auth_date"]!!.toLong(),
                hash = formData["hash"]!!
            )

            jsonLogger.logToJson(telegramUser)

            if (!users.existsByTelegramId(formData["id"]!!.toLong())) {
                val firstName = formData["first_name"]
                val lastName = formData["last_name"]
                val name = when {
                    firstName == null && lastName == null -> ""
                    firstName == null -> lastName!!
                    lastName == null -> firstName
                    else -> "$firstName $lastName"
                }

                users.add(
                    UserModel(
                        name = name,
                        tg_id = formData["id"]!!.toLong()
                    )
                )
            }


            return Response(Status.FOUND)
                .header("Location", "/")
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
        val config = AppConfig()
        val authSalt = config.webConfig.authSalt

        val userModel = users.findByTelegramId(user.id)
            ?: throw IllegalStateException("User with tg_id=${user.id} not found")

        val userId = userModel.id
        val username = user.username ?: ""
        val rawData = "$userId:$username"
        val signature = hmacSign(rawData, authSalt)

        return Cookie(
            name = "tg_auth",
            value = "$rawData:$signature",
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
