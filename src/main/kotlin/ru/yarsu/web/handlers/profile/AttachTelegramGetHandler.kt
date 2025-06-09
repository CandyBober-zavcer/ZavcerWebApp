package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import ru.yarsu.db.UserData
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.models.telegram.JsonLogger

class AttachTelegramGetHandler(
    private val users: UserData,
    private val jsonLogger: JsonLogger,
    private val botToken: String,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return try {
            val user =
                UserModelLens(request)
                    ?: return Response(UNAUTHORIZED).body("Пользователь не авторизован")

            val paramsMap =
                request.uri.query
                    .toParameters()
                    .associate { it.first to (it.second ?: "") }

            val telegramData = jsonLogger.parseTelegramQuery(paramsMap, botToken)

            val now = System.currentTimeMillis() / 1000
            if (now - telegramData.authDate > 86400) {
                return Response(BAD_REQUEST).body("Данные Telegram устарели")
            }

            users.attachTelegram(user.id, telegramData.id)
            Response(FOUND).header("Location", "/profile/${user.id}")
        } catch (e: Exception) {
            e.printStackTrace()
            Response(BAD_REQUEST).body("Ошибка при привязке Telegram: ${e.message}")
        }
    }
}
