package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.routing.path
import org.jetbrains.exposed.sql.OffsetWindowFrameBound
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class RejectOwnerPostHandler(
    private val users: UserData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val ownerId =
            request.path("id")?.toIntOrNull()
                ?: return Response(BAD_REQUEST).body("Неверный ID пользователя")

        val user =
            users.getOwnerByIdIfRolePendingOwner(ownerId)
                ?: return Response(NOT_FOUND).body("Преподаватель не найден")

        users.rejectOwnerRequest(ownerId)

        if (user.tg_id > 0L) {
            TelegramService.notifyOwnerRejected(user.tg_id)
        }

        return Response(FOUND).header("Location", "/upgrade/owners")
    }
}
