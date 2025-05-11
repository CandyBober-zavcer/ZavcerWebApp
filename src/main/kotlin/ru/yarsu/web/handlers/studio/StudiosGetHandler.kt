package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import ru.yarsu.db.StudiosData
import ru.yarsu.db.TeachersData
import ru.yarsu.web.models.studio.StudiosVM
import ru.yarsu.web.models.teacher.TeachersVM
import ru.yarsu.web.templates.ContextAwareViewRender

class StudiosGetHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val studios = StudiosData().fillStudios()
        val viewModel = StudiosVM(studios)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}