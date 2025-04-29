package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.models.studio.StudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class StudioGetHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)
        val viewModel = StudioVM(
            "Hello there!",
            user?.id?.toString() ?: "null"
        )
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}