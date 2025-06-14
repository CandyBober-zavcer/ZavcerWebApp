package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.lens.*
import ru.yarsu.config.AppConfig
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.models.telegram.AuthUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class EmailAuthPostHandler(
    private val databaseController: DatabaseController,
    private val config: AppConfig,
) : HttpHandler {
    private val loginLens = FormField.nonEmptyString().required("login")
    private val passwordLens = FormField.nonEmptyString().required("password")
    private val generatedAtLens = FormField.long().required("formGeneratedAt")
    private val honeypotLens = FormField.string().defaulted("website", "")

    private val formLens =
        Body
            .webForm(
                Validator.Strict,
                loginLens,
                passwordLens,
                generatedAtLens,
                honeypotLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val form =
            try {
                formLens(request)
            } catch (e: LensFailure) {
                return Response(Status.BAD_REQUEST).body("Неверные данные формы")
            }

        val honeypot = honeypotLens(form)
        if (honeypot.isNotBlank()) {
            return Response(Status.BAD_REQUEST).body("Похоже, вы бот.")
        }

        val generatedAt = generatedAtLens(form)
        val now = System.currentTimeMillis()
        val timeDiff = now - generatedAt

        if (timeDiff < 1000) {
            println("Форма отправлена слишком быстро: ${timeDiff}мс")
            return Response(Status.BAD_REQUEST).body("Слишком быстрая отправка формы")
        }

        val login = loginLens(form)
        val password = passwordLens(form)

        val user =
            login.let { databaseController.getUserByLogin(it) }
                ?: return Response(Status.UNAUTHORIZED).body("Пользователь не найден")

        if (!databaseController.verifyPassword(user, password)) {
            return Response(Status.UNAUTHORIZED).body("Неверный пароль")
        }

        return Response(Status.FOUND)
            .header("Location", "/")
            .cookie(createAuthCookie(user, config.webConfig.authSalt))
    }

    private fun createAuthCookie(
        user: User,
        salt: String,
    ): Cookie {
        val login = user.login ?: "unknown"
        val rawData = "${user.id}:$login"
        val signature = AuthUtils.hmacSign(rawData, salt)

        return Cookie(
            name = "auth",
            value = "$rawData:$signature",
            path = "/",
            httpOnly = true,
            secure = true,
            sameSite = SameSite.Strict,
            expires = ZonedDateTime.of(LocalDateTime.now().plusDays(7), ZoneId.of("Europe/Moscow")).toInstant(),
        )
    }
}
