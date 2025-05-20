package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.TeachersData
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.models.teacher.TeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeacherGetHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)
        val teacherId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        val teacher = TeachersData().getTeacherById(teacherId)
            ?: return Response(Status.NOT_FOUND).body("Преподаватель не найден")

        val viewModel = TeacherVM(
            teacher,
            user?.id?.toString() ?: "null"
        )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}
