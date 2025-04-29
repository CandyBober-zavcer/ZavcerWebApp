package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.client.OkHttp
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class TeacherPostHandler(
    private val telegramBotToken: String,
    private val teacherUserId: String
) : HttpHandler {

    override fun invoke(request: Request): Response {
        return try {
            val body = request.bodyString()
            val mapper = jacksonObjectMapper()
            val data = mapper.readValue<FormData>(body)

            val userMessage = buildString {
                appendLine("🎵 Вы записались на занятие:")
                appendLine("📅 Дата: ${data.date}")
                appendLine("⏰ Время: ${data.time}")
                appendLine("🎸 Инструмент: ${data.instrument}")
                appendLine("👨‍🏫 Преподаватель ID: $teacherUserId")
            }

            val teacherMessage = buildString {
                appendLine("🆕 Новая запись на занятие:")
                appendLine("📅 Дата: ${data.date}")
                appendLine("⏰ Время: ${data.time}")
                appendLine("🎸 Инструмент: ${data.instrument}")
                appendLine("👤 Имя: ${data.name}")
                appendLine("📞 Телефон: ${data.phone}")
                appendLine("🧑‍🎓 Ученик ID: ${data.userId}")
            }


            val client = OkHttp()

            fun sendMessage(chatId: String, message: String): Response {
                return client(
                    Request(POST, "https://api.telegram.org/bot$telegramBotToken/sendMessage")
                        .header("Content-Type", "application/json")
                        .body(
                            mapper.writeValueAsString(
                                mapOf("chat_id" to chatId, "text" to message)
                            )
                        )
                )
            }

            val userResponse = sendMessage(data.userId, userMessage)
            val adminResponse = sendMessage(teacherUserId, teacherMessage)

            if (userResponse.status.successful && adminResponse.status.successful) {
                Response(Status.OK).body("""{"status":"ok"}""")
            } else {
                Response(Status.BAD_GATEWAY).body("""{"error":"Telegram send failed"}""")
            }

        } catch (e: Exception) {
            Response(Status.BAD_REQUEST).body("""{"error":"${e.message}"}""")
        }
    }

    data class FormData(
        val name: String,
        val phone: String,
        val date: String,
        val time: String,
        val instrument: String,
        val userId: String
    )
}

