package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.web.models.teacher.TeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeacherHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = TeacherVM("Hello there!")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}