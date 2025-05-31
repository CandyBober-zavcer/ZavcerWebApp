package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import ru.yarsu.db.UserData
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.models.telegram.JsonLogger
import java.lang.Exception

class AttachTelegramHandler(
    private val users: UserData,
    private val jsonLogger: JsonLogger,
    private val botToken: String,
) : HttpHandler {

    override fun invoke(request: Request): Response {
        return try {
            val body = request.bodyString()
            val telegramData = jsonLogger.parseTelegramData(body, botToken)

            val user = UserModelLens(request) ?: return Response(Status.UNAUTHORIZED).body("Пользователь не авторизован")

            users.attachTelegram(user.id, telegramData.id)

            Response(Status.OK).body("Telegram успешно привязан")
        } catch (e: Exception) {
            e.printStackTrace()
            Response(Status.BAD_REQUEST).body("Ошибка при привязке Telegram: ${e.message}")
        }
    }

}
