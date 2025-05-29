package ru.yarsu.web.domain.models.telegram

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

data class TelegramUser(
    val id: Long,
    val username: String?,
    val first_name: String? = null,
    val last_name: String? = null,
    val photo_url: String? = null,
    val auth_date: Long? = null,
    val hash: String? = null,
)

class JsonLogger(
    private val filePath: String,
) {
    private val objectMapper =
        ObjectMapper().apply {
            registerKotlinModule()
        }

    fun logToJson(user: TelegramUser) {
        try {
            val file =
                File(filePath).apply {
                    parentFile?.mkdirs()
                }

            val users =
                if (file.exists() && file.length() > 0) {
                    objectMapper
                        .readValue(
                            file,
                            object : com.fasterxml.jackson.core.type.TypeReference<List<TelegramUser>>() {},
                        ).toMutableList()
                } else {
                    mutableListOf()
                }

            val existingIndex = users.indexOfFirst { it.id == user.id }
            if (existingIndex >= 0) {
                users[existingIndex] = user
            } else {
                users.add(user)
            }

            objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(file, users)
        } catch (e: Exception) {
            println("Error saving user data: ${e.message}")
        }
    }
}
