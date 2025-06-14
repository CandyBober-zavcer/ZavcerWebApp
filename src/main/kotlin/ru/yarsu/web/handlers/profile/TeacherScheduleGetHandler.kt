package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.databasecontrollers.JsonController
import ru.yarsu.web.models.profile.ProfileVM
import ru.yarsu.web.models.profile.TeacherScheduleVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeacherScheduleGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID профиля")
        val user =
            databaseController.getUserById(userId)
                ?: return Response(Status.NOT_FOUND).body("Профиль не найден")
        val viewModel = TeacherScheduleVM(user, JsonController.getAvailableDatesForTeacherJson(userId), JsonController.getBlockedDatesForTeacherJson(userId), JsonController.getAvailableDatesForSpotJson(userId), JsonController.getBlockedDatesForUserSpotsJson(userId))
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}