package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.db.UserData
import ru.yarsu.web.models.teacher.TeachersVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeachersGetHandler(private val htmlView: ContextAwareViewRender, private val users: UserData) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = TeachersVM(users.getTeachers())
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}