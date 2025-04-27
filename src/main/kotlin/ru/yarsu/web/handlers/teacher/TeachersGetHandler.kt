package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.web.models.teacher.TeachersVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeachersGetHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = TeachersVM("Hello there!")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}