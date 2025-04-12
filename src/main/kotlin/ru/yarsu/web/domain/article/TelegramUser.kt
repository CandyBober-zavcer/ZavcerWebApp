package ru.yarsu.web.domain.article

data class TelegramUser(
    val id: Long,
    val first_name: String,
    val last_name: String? = null,
    val username: String? = null,
    val photo_url: String? = null,
    val auth_date: Long,
    val hash: String
)