package ru.yarsu.web.handlers.admin

import org.http4k.core.*
import ru.yarsu.db.UserData
import ru.yarsu.web.models.admin.UserListVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UserListHandler(
    private val htmlView: ContextAwareViewRender,
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = UserListVM(users.getAll())
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
