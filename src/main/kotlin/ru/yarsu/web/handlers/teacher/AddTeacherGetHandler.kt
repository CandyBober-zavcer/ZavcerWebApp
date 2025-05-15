package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.db.TeachersData
import ru.yarsu.web.models.teacher.AddTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class AddTeacherGetHandler(private val htmlView: ContextAwareViewRender): HttpHandler {

    override fun invoke(request: Request): Response {
        val teachers = TeachersData().fillTeachers()
        val viewModel = AddTeacherVM(teachers)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}