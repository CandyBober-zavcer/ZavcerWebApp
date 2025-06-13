package ru.yarsu.web.handlers.teacher

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class TeacherPostHandler(
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val user =
            UserModelLens(request)
                ?: return Response(UNAUTHORIZED).body("Пользователь не авторизован")

        val teacherId =
            request.path("id")?.toIntOrNull()
                ?: return Response(BAD_REQUEST).body("Некорректный ID")

        val teacher =
            users.getTeacherById(teacherId)
                ?: return Response(NOT_FOUND).body("Преподаватель не найден")

        val teacherHasTelegram = teacher.tg_id > 0L
        val studentHasTelegram = user.tg_id > 0L

        if (teacherHasTelegram) {
            TelegramService.teacherNotification(teacher.tg_id, user.tg_id)
        }

        if (studentHasTelegram) {
            TelegramService.studentNotification(teacher.tg_id, user.tg_id)
        }

        return Response(FOUND).header("Location", "/teachers")
    }
}
