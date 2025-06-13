package ru.yarsu.web.handlers.admin

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class AddOwnerRoleHandler(
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId = request.path("id")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)

        val success = users.addOwnerRoleById(userId)
        if (!success) {
            return Response(Status.NOT_FOUND)
        }

        val user = users.getById(userId)
        val tgId = user?.tg_id ?: 0L
        if (tgId > 0L) {
            TelegramService.notifyRoleAdded(tgId, "owner")
        }

        return Response(Status.FOUND).header("Location", "/admin/user/$userId")
    }
}
