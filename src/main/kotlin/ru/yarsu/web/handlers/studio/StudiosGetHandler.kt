package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import ru.yarsu.web.models.studio.StudiosVM
import ru.yarsu.web.templates.ContextAwareViewRender

class StudiosGetHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = StudiosVM("Hello there!")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}