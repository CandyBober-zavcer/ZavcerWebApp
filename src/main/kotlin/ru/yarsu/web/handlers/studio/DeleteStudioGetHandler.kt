package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.StudiosData
import ru.yarsu.web.models.studio.DeleteStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteStudioGetHandler(private val htmlView: ContextAwareViewRender, private val studios: StudiosData): HttpHandler {

    override fun invoke(request: Request): Response {
        val studioId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID студии")

        val studio = studios.getStudioById(studioId)
            ?: return Response(Status.NOT_FOUND).body("Студия не найдена")

        val viewModel = DeleteStudioVM(studio)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}