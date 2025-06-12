package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.RoleEnums
import ru.yarsu.web.models.teacher.TeachersVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeachersGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = TeachersVM(databaseController.getAllUsersByRole(RoleEnums.TEACHER.id))
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
