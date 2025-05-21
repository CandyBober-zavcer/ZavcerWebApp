package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import ru.yarsu.web.models.auth.AuthVM
import ru.yarsu.web.templates.ContextAwareViewRender


class AuthGetHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = AuthVM("Hello there!")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
