package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.lens.*
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.SessionStorage

class AuthPostHandler(
    private val userData: UserData,
    private val sessionStorage: SessionStorage
) : HttpHandler {

    private val emailLens = FormField.nonEmptyString().map({ it.trim() }, { it }).required("email")
    private val passwordLens = FormField.nonEmptyString().required("password")
    private val formLens = Body.webForm(
        Validator.Strict,
        emailLens,
        passwordLens
    ).toLens()

    override fun invoke(request: Request): Response {
        val form = try {
            formLens(request)
        } catch (e: LensFailure) {
            println("Ошибка парсинга формы: ${e.failures}")
            return Response(Status.BAD_REQUEST).body("Неверные данные формы")
        }

        val email = emailLens(form)
        val password = passwordLens(form)

        val user = userData.getByEmail(email)
            ?: return Response(Status.UNAUTHORIZED).body("Неверный email или пароль")

        if (!userData.verifyPassword(user, password)) {
            return Response(Status.UNAUTHORIZED).body("Неверный email или пароль")
        }

        val session = sessionStorage.create(user.id)
        return Response(Status.FOUND)
            .header("Location", "/")
            .cookie(session.cookie)
    }

}
