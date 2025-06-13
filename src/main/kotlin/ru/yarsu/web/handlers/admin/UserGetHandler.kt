package ru.yarsu.web.handlers.admin

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.models.admin.UserVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UserGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID")

        val user =
            users.getById(userId)
                ?: return Response(Status.NOT_FOUND).body("Пользователь не найден")

        val hasTeacher = users.getTeacherById(userId) != null
        val hasOwner = users.getUserIfOwner(userId) != null

        val viewModel = UserVM(user, hasTeacher, hasOwner)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
