package ru.yarsu.web.handlers.admin

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class AddTeacherRoleHandler(
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId = request.path("id")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        val success = databaseController.addTeacherRoleById(userId)
        if (!success) {
            return Response(Status.NOT_FOUND)
        }
        val user = databaseController.getUserById(userId)
        val tgId = user?.tg_id ?: 0L
        if (tgId > 0L) {
            TelegramService.notifyRoleAdded(tgId, "teacher")
        }
        return Response(Status.FOUND).header("Location", "/admin/user/$userId")
    }
}