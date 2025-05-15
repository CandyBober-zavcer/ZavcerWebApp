package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import ru.yarsu.web.models.studio.EditStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditStudioGetHandler(private val htmlView: ContextAwareViewRender):HttpHandler {

    override fun invoke(request: Request): Response {


        val viewModel = EditStudioVM(

        )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}