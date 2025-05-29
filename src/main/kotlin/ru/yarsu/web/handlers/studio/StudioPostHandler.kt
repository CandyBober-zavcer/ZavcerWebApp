package ru.yarsu.web.handlers.studio

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.core.Method.POST

class StudioPostHandler(
    private val telegramBotToken: String,
    private val adminUserId: String,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        try {
            val body = request.bodyString()
            val mapper = jacksonObjectMapper()
            val data = mapper.readValue<FormData>(body)

            val userMessage =
                buildString {
                    appendLine("🎙 Студия успешно забронирована!")
                    appendLine("📅 Дата: ${data.date}")
                    appendLine("⏰ Время: с ${data.startTime} до ${data.endTime}")
                    appendLine("🧑‍🎤 Администратор ID: $adminUserId")
                }

            val adminMessage =
                buildString {
                    appendLine("🆕 Новая бронь студии:")
                    appendLine("📅 Дата: ${data.date}")
                    appendLine("⏰ Время: с ${data.startTime} до ${data.endTime}")
                    appendLine("🧑‍🎤 Пользователь ID: ${data.userId}")
                }

            val client = OkHttp()

            fun sendMessage(
                chatId: String,
                message: String,
            ): Response =
                client(
                    Request(POST, "https://api.telegram.org/bot$telegramBotToken/sendMessage")
                        .header("Content-Type", "application/json")
                        .body(
                            mapper.writeValueAsString(
                                mapOf("chat_id" to chatId, "text" to message),
                            ),
                        ),
                )

            val userResponse = sendMessage(data.userId, userMessage)
            val adminResponse = sendMessage(adminUserId, adminMessage)

            if (userResponse.status.successful && adminResponse.status.successful) {
                Response(Status.OK).body("""{"status":"ok"}""")
            } else {
                Response(Status.BAD_GATEWAY).body("""{"error":"Telegram send failed"}""")
            }
        } catch (e: Exception) {
            Response(Status.BAD_REQUEST).body("""{"error":"${e.message}"}""")
        }

    data class FormData(
        val date: String,
        val startTime: String,
        val endTime: String,
        val userId: String,
    )
}
