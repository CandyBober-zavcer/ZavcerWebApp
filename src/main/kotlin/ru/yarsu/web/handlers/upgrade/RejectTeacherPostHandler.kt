package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class RejectTeacherPostHandler(
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val teacherId =
            request.path("id")?.toIntOrNull()
                ?: return Response(BAD_REQUEST).body("Неверный ID пользователя")

        val user =
            databaseController.getTeacherByIdIfRolePendingTeacher(teacherId)
                ?: return Response(NOT_FOUND).body("Преподаватель не найден")

        databaseController.updateTeacherRequest(teacherId, false)

        if (user.tg_id > 0L) {
            TelegramService.notifyTeacherRejected(user.tg_id)
        }

        return Response(FOUND).header("Location", "/upgrade/teachers")
    }
}
