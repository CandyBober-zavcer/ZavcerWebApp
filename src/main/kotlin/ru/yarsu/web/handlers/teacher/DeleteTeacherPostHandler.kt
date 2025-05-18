package ru.yarsu.web.handlers.teacher

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import ru.yarsu.db.TeachersData
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteTeacherPostHandler(private val htmlView: ContextAwareViewRender, private val teachers: TeachersData): HttpHandler {

    override fun invoke(request: Request): Response {
        val teacherId = request.form("teacherId")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        teachers.removeTeacher(teacherId)

        return Response(Status.FOUND).header("Location", "/teachers")
    }

}