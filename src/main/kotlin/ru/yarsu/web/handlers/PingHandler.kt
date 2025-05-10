package ru.yarsu.web.handlers

import org.http4k.core.*
import ru.yarsu.web.domain.models.telegram.AuthUtils

class PingHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)

        return if (user != null) {
            Response(Status.OK).body("Welcome, ${user.username}!")
        } else {
            Response(Status.FOUND).header("ru.yarsu.web.domain.article.Location", "/login")
        }
    }
}
