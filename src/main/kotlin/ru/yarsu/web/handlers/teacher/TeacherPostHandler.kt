package ru.yarsu.web.handlers.teacher

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.body.form
import org.http4k.format.Jackson.auto
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.UserData
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.models.BookingRequest
import ru.yarsu.web.domain.models.telegram.service.TelegramService

data class FormData(
    val date: String,
    val time: String,
    val userId: Long

)

class TeacherPostHandler(private val databaseController: DatabaseController) : HttpHandler {
    private val formDataLens = Body.auto<FormData>().toLens()
    override fun invoke(request: Request): Response {
        val formData = try {
            formDataLens(request)
        } catch (e: Exception) {
            return Response(BAD_REQUEST).body("Неверный формат данных: ${e.message}")
        }

        val user = UserModelLens(request)
            ?: return Response(UNAUTHORIZED).body("Пользователь не авторизован")

        val teacherId = request.path("id")?.toIntOrNull()
            ?: return Response(BAD_REQUEST).body("Некорректный ID")

        val teacher = databaseController.getTeacherById(teacherId)
            ?: return Response(NOT_FOUND).body("Преподаватель не найден")

        val teacherHasTelegram = teacher.tg_id > 0L
        val studentHasTelegram = user.tg_id > 0L

        if (teacherHasTelegram) {
            TelegramService.teacherNotification(teacher.tg_id, user.tg_id)
        }

        if (studentHasTelegram) {
            TelegramService.studentNotification(teacher.tg_id, user.tg_id)
        }
        databaseController.occ

        println("Полученные данные бронирования:")
        println("Дата: ${formData.date}")
        println("Время: ${formData.time}")
        println("UserId (из формы): ${formData.userId}")

        return Response(FOUND).header("Location", "/teachers")
    }

}
