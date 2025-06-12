package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.models.upgrade.UpgradeTeacherProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UpgradeTeacherProfileGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val teacherId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID")

        val teacher =
            users.getTeacherByIdIfRolePendingTeacher(teacherId)
                ?: return Response(Status.NOT_FOUND).body("Пользователь не найден или уже не в статусе ожидания")

        val viewModel = UpgradeTeacherProfileVM(teacher)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
