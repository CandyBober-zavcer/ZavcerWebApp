package ru.yarsu.web.handlers.auth

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.TokenStorage
import ru.yarsu.web.models.auth.ConfirmSuccessVM
import ru.yarsu.web.templates.ContextAwareTemplateRenderer

class EmailConfirmHandler(
    private val tokenStorage: TokenStorage,
    private val userData: UserData,
    private val renderer: ContextAwareTemplateRenderer
) : HttpHandler {

    override fun invoke(request: Request): Response {
        val token = request.query("token") ?: return Response(Status.BAD_REQUEST).body("Токен не указан")

        val userId = tokenStorage.findUserIdByToken(token)
            ?: return Response(Status.BAD_REQUEST).body("Недействительный или истёкший токен")

        val user = userData.getById(userId)
            ?: return Response(Status.NOT_FOUND).body("Пользователь не найден")

        userData.confirmUser(userId)
        tokenStorage.remove(token)

        val model = ConfirmSuccessVM(redirectUrl = "/auth/signin")
        val html = renderer(emptyMap(), model)

        return Response(Status.OK).body(html)
    }
}

