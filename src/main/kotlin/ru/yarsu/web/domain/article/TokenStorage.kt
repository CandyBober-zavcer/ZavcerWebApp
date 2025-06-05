package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.article.ConfirmationToken
import ru.yarsu.web.domain.article.TokenType
import java.util.*

class TokenStorage {
    private val tokens = mutableMapOf<String, ConfirmationToken>()

    fun generateConfirmationToken(userId: Int): String {
        val token = UUID.randomUUID().toString()
        tokens[token] = ConfirmationToken(
            token = token,
            userId = userId,
            expiresAt = System.currentTimeMillis() + 15 * 60 * 1000,
            type = TokenType.CONFIRMATION
        )
        return token
    }

    fun saveResetPasswordToken(email: String, token: String, durationMinutes: Long = 60) {
        tokens[token] = ConfirmationToken(
            token = token,
            email = email,
            expiresAt = System.currentTimeMillis() + durationMinutes * 60 * 1000,
            type = TokenType.RESET_PASSWORD
        )
    }

    fun findUserIdByToken(token: String): Int? {
        val confirmation = tokens[token]
        return if (
            confirmation != null &&
            confirmation.type == TokenType.CONFIRMATION &&
            confirmation.expiresAt > System.currentTimeMillis()
        ) {
            confirmation.userId
        } else null
    }

    fun findEmailByResetToken(token: String): String? {
        val entry = tokens[token]
        return if (
            entry != null &&
            entry.type == TokenType.RESET_PASSWORD &&
            entry.expiresAt > System.currentTimeMillis()
        ) {
            entry.email
        } else null
    }

    fun remove(token: String) {
        tokens.remove(token)
    }
}
