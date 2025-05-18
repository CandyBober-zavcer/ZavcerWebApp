package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.core.body.form
import ru.yarsu.db.StudiosData
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteStudioPostHandler (private val htmlView: ContextAwareViewRender, private val studios: StudiosData): HttpHandler {

    override fun invoke(request: Request): Response {
        val studioId = request.form("studioId")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID студии")

        studios.removeStudio(studioId)

        return Response(Status.FOUND).header("Location", "/studios")
    }

}