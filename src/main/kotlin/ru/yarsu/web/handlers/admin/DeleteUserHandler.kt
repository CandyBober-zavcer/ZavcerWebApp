package ru.yarsu.web.handlers.admin

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.yarsu.db.SpotData
import ru.yarsu.db.UserData

class DeleteUserHandler(
    private val users: UserData,
    private val spots: SpotData
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId = request.path("id")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)

        val allSpots = spots.getAll()
        allSpots.forEach { spot ->
            if (userId in spot.owners) {
                if (spot.owners.size == 1) {
                    // Пользователь единственный владелец — удаляем студию целиком
                    spots.deleteById(spot.id)
                } else {
                    // Несколько владельцев — убираем пользователя из списка
                    val newOwners = spot.owners.filter { it != userId }
                    val updatedSpot = spot.copy(owners = newOwners)
                    spots.update(spot.id, updatedSpot)
                }
            }
        }

        val deleted = users.deleteById(userId)
        if (!deleted) {
            return Response(Status.NOT_FOUND).body("Пользователь с id=$userId не найден")
        }

        return Response(Status.FOUND).header("Location", "/admin/users")
    }
}
