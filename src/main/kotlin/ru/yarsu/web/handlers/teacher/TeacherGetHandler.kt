package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.models.teacher.TeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeacherGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val user: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val teacherId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        val teacher =
            user.getTeacherById(teacherId)
                ?: return Response(Status.NOT_FOUND).body("Преподаватель не найден")

        val viewModel =
            TeacherVM(
                teacher,
            )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
