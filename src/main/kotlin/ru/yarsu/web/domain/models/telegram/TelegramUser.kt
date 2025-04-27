package ru.yarsu.web.domain.models.telegram

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.http4k.core.Body
import org.http4k.format.Jackson.auto
import java.io.File

data class TelegramUser(
    val id: Long,
    val first_name: String,
    val last_name: String?,
    val username: String?,
    val photo_url: String?,
    val auth_date: Long,
    val hash: String
)

class JsonLogger(private val filePath: String) {
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerKotlinModule()
        writerWithDefaultPrettyPrinter()
    }

    fun logToJson(user: TelegramUser) {
        try {
            val file = File(filePath)
            file.parentFile?.mkdirs()

            val existingData: MutableList<TelegramUser> = if (file.exists() && file.length() > 0) {
                objectMapper.readValue(file, object : com.fasterxml.jackson.core.type.TypeReference<List<TelegramUser>>() {})
                    .toMutableList()
            } else {
                mutableListOf()
            }

            val existingUserIndex = existingData.indexOfFirst { it.id == user.id }

            if (existingUserIndex >= 0) {
                existingData[existingUserIndex] = user
                println("Обновлены данные пользователя с id=${user.id}")
            } else {
                existingData.add(user)
                println("Добавлен новый пользователь с id=${user.id}")
            }

            objectMapper.writeValue(file, existingData)
            println("Данные сохранены в: ${file.absolutePath}")
        } catch (e: Exception) {
            println("Ошибка при записи в JSON: ${e.message}")
            e.printStackTrace()
        }
    }
}

val telegramUserLens = Body.auto<TelegramUser>().toLens()