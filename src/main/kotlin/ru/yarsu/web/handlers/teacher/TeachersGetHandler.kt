package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.db.TeachersData
import ru.yarsu.web.models.teacher.TeachersVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeachersGetHandler(private val htmlView: ContextAwareViewRender, private val teachers: TeachersData) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = TeachersVM(teachers.getAllTeachers())
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}