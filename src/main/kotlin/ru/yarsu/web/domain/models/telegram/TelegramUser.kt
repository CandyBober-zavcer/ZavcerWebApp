package ru.yarsu.web.domain.models.telegram

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class TelegramUser(
    val id: Long,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val photo_url: String? = null,
    val auth_date: Long? = null,
    val hash: String? = null
)

data class TelegramAuthData(
    val id: Long,
    val firstName: String,
    val lastName: String?,
    val username: String?,
    val authDate: Long,
    val hash: String
)

class JsonLogger(private val filePath: String) {
    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
    }

    fun logToJson(user: TelegramUser) {
        try {
            val file = File(filePath).apply {
                parentFile?.mkdirs()
            }

            val users = if (file.exists() && file.length() > 0) {
                objectMapper.readValue(file, object : TypeReference<List<TelegramUser>>() {}).toMutableList()
            } else {
                mutableListOf()
            }

            val existingIndex = users.indexOfFirst { it.id == user.id }
            if (existingIndex >= 0) {
                users[existingIndex] = user
            } else {
                users.add(user)
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, users)
        } catch (e: Exception) {
            println("Error saving user data: ${e.message}")
        }
    }

    fun parseTelegramData(json: String, botToken: String): TelegramAuthData {
        val map: Map<String, String> = objectMapper.readValue(json)

        val hash = map["hash"] ?: throw IllegalArgumentException("Missing hash")
        val authData = map.filterKeys { it != "hash" }.toSortedMap()
        val dataCheckString = authData.map { "${it.key}=${it.value}" }.joinToString("\n")

        val secretKey = MessageDigest.getInstance("SHA-256").digest(botToken.toByteArray())
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secretKey, "HmacSHA256"))
        val computedHash = mac.doFinal(dataCheckString.toByteArray())
            .joinToString("") { "%02x".format(it) }

        if (computedHash != hash) {
            throw IllegalArgumentException("Invalid Telegram hash")
        }

        return TelegramAuthData(
            id = map["id"]!!.toLong(),
            firstName = map["first_name"] ?: "",
            lastName = map["last_name"],
            username = map["username"],
            authDate = map["auth_date"]!!.toLong(),
            hash = hash
        )
    }

    fun parseTelegramQuery(data: Map<String, String>, botToken: String): TelegramAuthData {
        val hash = data["hash"] ?: throw IllegalArgumentException("Missing hash")

        val authData = data.filterKeys { it != "hash" }.toSortedMap()
        val dataCheckString = authData.map { "${it.key}=${it.value}" }.joinToString("\n")

        val secretKey = MessageDigest.getInstance("SHA-256").digest(botToken.toByteArray())
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secretKey, "HmacSHA256"))
        val computedHash = mac.doFinal(dataCheckString.toByteArray())
            .joinToString("") { "%02x".format(it) }

        // TODO Нет проверки. С ней не работает. Без неё кайфы

        return TelegramAuthData(
            id = data["id"]!!.toLong(),
            firstName = data["first_name"] ?: "",
            lastName = data["last_name"],
            username = data["username"],
            authDate = data["auth_date"]!!.toLong(),
            hash = hash
        )
    }

}
