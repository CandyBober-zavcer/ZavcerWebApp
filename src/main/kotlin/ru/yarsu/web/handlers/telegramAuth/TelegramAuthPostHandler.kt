package ru.yarsu.web.handlers.telegramAuth

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import ru.yarsu.web.domain.models.telegram.JsonLogger
import ru.yarsu.web.domain.models.telegram.telegramUserLens
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TelegramAuthPostHandler : HttpHandler {
    private val jsonLogger = JsonLogger("user_data.json")

    override fun invoke(request: Request): Response {
        return try {
            // Логируем тело запроса для отладки
            val bodyString = request.bodyString()
            println("Тело запроса: $bodyString")

            // Извлекаем TelegramUser из запроса
            val telegramUser = telegramUserLens(request)
            println("Получен пользователь: $telegramUser")

            // Логируем данные в JSON
            jsonLogger.logToJson(telegramUser)

            // Создаем куки с id пользователя
            val cookie = Cookie(
                name = "auth",
                value = telegramUser.id.toString(),
                httpOnly = true,
                expires = ZonedDateTime.of(LocalDateTime.now().plusDays(7), ZoneId.of("Europe/Moscow")).toInstant()
            )

            // Перенаправляем пользователя на главную страницу
            Response(Status.FOUND)
                .cookie(cookie)
                .header("Location", "/")

        } catch (e: Exception) {
            println("Ошибка при обработке данных пользователя: ${e.message}")
            e.printStackTrace()
            Response(Status.BAD_REQUEST).body("Ошибка при авторизации!")
        }
    }
}