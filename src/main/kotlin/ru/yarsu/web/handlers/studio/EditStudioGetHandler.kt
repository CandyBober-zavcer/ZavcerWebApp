package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.StudiosData
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.models.studio.EditStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditStudioGetHandler(private val htmlView: ContextAwareViewRender): HttpHandler {

    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)
        val studioId = request.path("id")?.toIntOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID студии")

        val teacher = StudiosData().fillStudios().find { it.id.toInt() == studioId }

        if (teacher == null) {
            return Response(Status.NOT_FOUND).body("Студия не найдена")
        }

        val viewModel = EditStudioVM(
            teacher,
            user?.id?.toString() ?: "null"
        )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}