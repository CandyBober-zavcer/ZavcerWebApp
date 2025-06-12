package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.models.profile.ProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ProfileGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID профиля")
        val user =
            users.getById(userId)
                ?: return Response(Status.NOT_FOUND).body("Профиль не найден")
        val viewModel = ProfileVM(user)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
