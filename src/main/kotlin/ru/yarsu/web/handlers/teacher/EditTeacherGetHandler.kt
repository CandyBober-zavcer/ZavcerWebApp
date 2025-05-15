package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.web.models.teacher.EditTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditTeacherGetHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {

        val viewModel = EditTeacherVM(
        )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}