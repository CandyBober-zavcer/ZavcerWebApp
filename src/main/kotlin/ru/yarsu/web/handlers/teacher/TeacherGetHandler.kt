package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.databasecontrollers.JsonController
import ru.yarsu.web.models.teacher.TeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeacherGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val teacherId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        val teacher =
            databaseController.getTeacherById(teacherId)
                ?: return Response(Status.NOT_FOUND).body("Преподаватель не найден")

        val viewModel =
            TeacherVM(
                teacher, JsonController.getAvailableDatesForTeacherJson(teacherId)
            )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
