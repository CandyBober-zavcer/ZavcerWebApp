package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.lens.*
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.article.SessionStorage

class AuthPostHandler(
    private val databaseController: DatabaseController,
    private val sessionStorage: SessionStorage,
) : HttpHandler {
    private val emailLens = FormField.nonEmptyString().map({ it.trim() }, { it }).required("email")
    private val passwordLens = FormField.nonEmptyString().required("password")
    private val generatedAtLens = FormField.long().required("formGeneratedAt")
    private val honeypotLens = FormField.string().defaulted("website", "")

    private val formLens =
        Body
            .webForm(
                Validator.Strict,
                emailLens,
                passwordLens,
                generatedAtLens,
                honeypotLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val form =
            try {
                formLens(request)
            } catch (e: LensFailure) {
                println("Ошибка парсинга формы: ${e.failures}")
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

        val email = emailLens(form)
        val password = passwordLens(form)

        val user =
            databaseController.getUserByEmail(email)
                ?: return Response(Status.UNAUTHORIZED).body("Неверный email или пароль")

        if (!databaseController.verifyPassword(user, password)) {
            return Response(Status.UNAUTHORIZED).body("Неверный email или пароль")
        }

        val session = sessionStorage.create(user.id)
        return Response(Status.FOUND)
            .header("Location", "/")
            .cookie(session.cookie)
    }
}
