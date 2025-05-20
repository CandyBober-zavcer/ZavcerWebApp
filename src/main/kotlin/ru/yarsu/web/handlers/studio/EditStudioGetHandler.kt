package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.StudiosData
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.models.studio.EditStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditStudioGetHandler(private val htmlView: ContextAwareViewRender): HttpHandler {

    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)
        val studioId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID студии")

        val studio = StudiosData().getStudioById(studioId)
            ?: return Response(Status.NOT_FOUND).body("Студия не найдена")
        val allInstruments = Instrument.entries

        val viewModel = EditStudioVM(
            studio,
            user?.id?.toString() ?: "null",
            allInstruments
        )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}