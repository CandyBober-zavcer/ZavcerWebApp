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

    fun sendMessage(chatId: Long, text: String) {
        val url = "https://api.telegram.org/bot$BOT_TOKEN/sendMessage"
        val payload = mapOf(
            "chat_id" to chatId,
            "text" to text
        )

        val client = HttpClient.newHttpClient()
        val mapper = jacksonObjectMapper()

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
            .build()

        client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}
