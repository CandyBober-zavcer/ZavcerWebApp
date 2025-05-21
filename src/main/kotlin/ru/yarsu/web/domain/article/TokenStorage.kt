package ru.yarsu.web.domain.article

import java.util.*

class TokenStorage {
    private val tokens = mutableMapOf<String, ConfirmationToken>()

    fun generateToken(userId: Int): String {
        val token = UUID.randomUUID().toString()
        tokens[token] = ConfirmationToken(token, userId, System.currentTimeMillis() + 15 * 60 * 1000)
        return token
    }

    fun findUserIdByToken(token: String): Int? {
        val confirmation = tokens[token]
        if (confirmation != null && confirmation.expiresAt > System.currentTimeMillis()) {
            return confirmation.userId
        }
        return null
    }

    fun remove(token: String) {
        tokens.remove(token)
    }
}
