package ru.yarsu.web.domain.models.telegram.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.yarsu.config.AppConfig
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object TelegramService {
    private val appConfig = AppConfig()
    private val BOT_TOKEN = appConfig.telegramConfig.botToken
    private val client = HttpClient.newHttpClient()
    private val mapper = jacksonObjectMapper()

    private fun send(
        chatId: Long,
        text: String,
    ) {
        if (chatId <= 0L) return

        val url = "https://api.telegram.org/bot$BOT_TOKEN/sendMessage"
        val payload = mapOf("chat_id" to chatId, "text" to text)

        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                .build()

        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun notifyTeacherSuccess(chatId: Long) {
        val text = "🎉 Ваша заявка на роль преподавателя одобрена! Добро пожаловать в команду!"
        send(chatId, text)
    }

    fun notifyTeacherRejected(chatId: Long) {
        val text = "🚫 К сожалению, ваша заявка на роль преподавателя была отклонена. Вы можете попробовать снова позже."
        send(chatId, text)
    }

    fun teacherNotification(teacherId: Long, studentId: Long) {
        if (teacherId <= 0L) return

        val text = if (studentId > 0L) {
            "Запись на занятие. Ученик: $studentId"
        } else {
            "Запись на занятие. Ученик не указан."
        }
        send(teacherId, text)
    }

    fun studentNotification(teacherId: Long, studentId: Long) {
        if (studentId <= 0L) return

        val text = if (teacherId > 0L) {
            "Запись на занятие. Учитель: $teacherId"
        } else {
            "Запись на занятие. Учитель не указан."
        }
        send(studentId, text)
    }

    fun spotOwnerNotification(ownerIds: List<Long>, spotId: Long, studentId: Long) {
        if (ownerIds.isEmpty()) return

        val text = if (studentId > 0L) {
            "Запись на репетиционную точку №$spotId. Ученик: $studentId"
        } else {
            "Запись на репетиционную точку №$spotId. Ученик не указан."
        }

        ownerIds.forEach { ownerId ->
            if (ownerId > 0L) {
                send(ownerId, text)
            }
        }
    }

    fun studentSpotNotification(ownerIds: List<Long>, spotId: Long, studentId: Long) {
        if (studentId <= 0L) return

        val validOwners = ownerIds.filter { it > 0L }
        val text = if (validOwners.isNotEmpty()) {
            "Запись на репетиционную точку №$spotId. Владелец(и): ${validOwners.joinToString(", ")}"
        } else {
            "Запись на репетиционную точку №$spotId. Владелец не указан."
        }

        send(studentId, text)
    }


}
