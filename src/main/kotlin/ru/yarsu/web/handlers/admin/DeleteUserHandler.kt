package ru.yarsu.web.handlers.admin

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.SpotData
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.models.telegram.service.TelegramService

class DeleteUserHandler(
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId = request.path("id")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)

        val user = databaseController.getUserById(userId)

        val allSpots = databaseController.getAllSpots()
        allSpots.forEach { spot ->
            if (userId in spot.owners) {
                if (spot.owners.size == 1) {
                    databaseController.deleteSpot(spot.id)
                } else {
                    // Несколько владельцев — убираем пользователя из списка
                    val newOwners = spot.owners.filter { it != userId }
                    val updatedSpot = spot.copy(owners = newOwners)
                    databaseController.updateSpotInfo(spot.id, updatedSpot)
                }
            }
        }

        val deleted = databaseController.deleteUser(userId)
        if (!deleted) {
            return Response(Status.NOT_FOUND).body("Пользователь с id=$userId не найден")
        }

        val tgId = user?.tg_id ?: 0L
        if (tgId > 0L) {
            TelegramService.notifyUserDeleted(tgId, user?.name)
        }

        return Response(Status.FOUND).header("Location", "/admin/users")
    }
}