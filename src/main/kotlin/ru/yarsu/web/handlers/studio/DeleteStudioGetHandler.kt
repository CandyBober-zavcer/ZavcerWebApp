package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import ru.yarsu.db.StudiosData
import ru.yarsu.web.models.studio.DeleteStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteStudioGetHandler(private val htmlView: ContextAwareViewRender): HttpHandler {

    override fun invoke(request: Request): Response {
        val studios = StudiosData().fillStudios()
        val viewModel = DeleteStudioVM(studios)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}