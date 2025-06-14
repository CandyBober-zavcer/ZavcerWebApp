package ru.yarsu.web.handlers.admin

import org.http4k.core.*
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.UserData
import ru.yarsu.web.models.admin.UserListVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UserListHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = UserListVM(databaseController.getAllUsers())
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}