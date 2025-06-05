package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class AcceptTeacherPostHandler(private val users: UserData) : HttpHandler {
    override fun invoke(request: Request): Response {
        val teacherId = request.path("id")?.toIntOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID")

        val user = users.getTeacherByIdIfRolePendingTeacher(teacherId)
            ?: return Response(NOT_FOUND).body("Пользователь не найден")

        val success = users.acceptTeacherRequest(teacherId)

        if (success && user.tg_id > 0L) {
            TelegramService.notifyTeacherSuccess(user.tg_id)
        }

        return if (success) {
            Response(FOUND).header("Location", "/upgrade/teachers")
        } else {
            Response(NOT_FOUND).body("Не удалось принять пользователя")
        }
    }
}
