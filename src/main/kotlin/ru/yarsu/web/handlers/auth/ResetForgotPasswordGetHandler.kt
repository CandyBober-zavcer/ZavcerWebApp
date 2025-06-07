package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import ru.yarsu.web.models.auth.ResetForgotPasswordGetVM
import ru.yarsu.web.templates.ContextAwareTemplateRenderer

class ResetForgotPasswordGetHandler(
    private val renderer: ContextAwareTemplateRenderer,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = ResetForgotPasswordGetVM()
        val html = renderer(emptyMap(), viewModel)
        return Response(Status.OK)
            .header("Content-Type", "text/html; charset=utf-8")
            .body(html)
    }
}
