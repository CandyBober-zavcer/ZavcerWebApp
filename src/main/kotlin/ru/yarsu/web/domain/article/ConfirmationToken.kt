package ru.yarsu.web.domain.article

data class ConfirmationToken(
    val token: String,
    val userId: Int? = null,
    val email: String? = null,
    val expiresAt: Long, // System.currentTimeMillis() + 15 * 60 * 1000
    val type: TokenType,
)
