package ru.yarsu.web.handlers

import org.http4k.core.*
import ru.yarsu.config.AppConfig
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.models.telegram.AuthUtils

class PingHandler(
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val config = AppConfig()
        val user = AuthUtils.getUserFromCookie(request, authSalt = config.webConfig.authSalt, users)

        return if (user != null) {
            Response(Status.OK).body("Welcome, ${user.name}!")
        } else {
            Response(Status.FOUND).header("Location", "/login")
        }
    }
}
