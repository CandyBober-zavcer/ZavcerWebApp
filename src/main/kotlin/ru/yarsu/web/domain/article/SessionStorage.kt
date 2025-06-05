package ru.yarsu.web.domain.article

import org.http4k.core.cookie.Cookie
import java.util.*

class SessionStorage {
    private val sessions = mutableMapOf<String, Int>()  // token -> userId

    fun create(userId: Int): Session {
        val token = UUID.randomUUID().toString()
        sessions[token] = userId
        return Session(token)
    }

    fun getUserId(token: String): Int? = sessions[token]

    fun remove(token: String) {
        sessions.remove(token)
    }

    data class Session(val token: String) {
        val cookie: Cookie
            get() = Cookie("session", token, path = "/", httpOnly = true)
    }
}
