package ru.yarsu.web.handlers.admin

import org.http4k.core.*
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.models.admin.UserVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UserGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID")

        val auth =
            UserModelLens(request)
                ?: return Response(UNAUTHORIZED).body("Пользователь не авторизован")

        val user =
            databaseController.getUserById(userId)
                ?: return Response(Status.NOT_FOUND).body("Пользователь не найден")

        val hasTeacher = databaseController.getTeacherById(userId) != null
        val hasOwner = databaseController.getOwnerById(userId) != null

        val viewModel = UserVM(user, hasTeacher, hasOwner, auth)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}