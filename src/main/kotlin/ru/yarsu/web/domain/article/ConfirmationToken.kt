package ru.yarsu.web.domain.article

data class ConfirmationToken(
    val token: String,
    val userId: Int,
    val expiresAt: Long, // System.currentTimeMillis() + 15 * 60 * 1000
)
