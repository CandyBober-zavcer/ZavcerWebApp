package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import java.time.Instant

class SignOutHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        val expiredTgCookie =
            Cookie(
                name = "tg_auth",
                value = "",
                path = "/",
                httpOnly = true,
                secure = true,
                expires = Instant.EPOCH,
            )

        val expiredSessionCookie =
            Cookie(
                name = "session",
                value = "",
                path = "/",
                httpOnly = true,
                secure = true,
                expires = Instant.EPOCH,
            )

        return Response(Status.FOUND)
            .header("Location", "/")
            .cookie(expiredTgCookie)
            .cookie(expiredSessionCookie)
    }
}
