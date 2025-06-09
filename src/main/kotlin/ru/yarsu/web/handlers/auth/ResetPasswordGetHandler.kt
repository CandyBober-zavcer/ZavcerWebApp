package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import ru.yarsu.web.domain.article.TokenStorage
import ru.yarsu.web.models.auth.ResetPasswordVM
import ru.yarsu.web.templates.ContextAwareTemplateRenderer

class ResetPasswordGetHandler(
    private val tokenStorage: TokenStorage,
    private val renderer: ContextAwareTemplateRenderer,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val token = request.query("token") ?: return Response(Status.BAD_REQUEST).body("Нет токена")

        val email =
            tokenStorage.findEmailByResetToken(token)
                ?: return Response(Status.BAD_REQUEST).body("Ссылка устарела или недействительна")

        val viewModel = ResetPasswordVM(token)
        val html = renderer(mapOf("model" to viewModel), viewModel)

        return Response(Status.OK)
            .header("Content-Type", "text/html; charset=utf-8")
            .body(html)
    }
}
