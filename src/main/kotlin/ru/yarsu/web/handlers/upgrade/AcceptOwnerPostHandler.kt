package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class AcceptOwnerPostHandler(
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val ownerId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID")

        val user =
            databaseController.getOwnerByIdIfRolePendingOwner(ownerId)
                ?: return Response(NOT_FOUND).body("Пользователь не найден")

        val success = databaseController.acceptOwnerRequest(ownerId)

        if (success && user.tg_id > 0L) {
            TelegramService.notifyOwnerSuccess(user.tg_id)
        }

        return if (success) {
            Response(FOUND).header("Location", "/upgrade/owners")
        } else {
            Response(NOT_FOUND).body("Не удалось принять пользователя")
        }
    }
}
