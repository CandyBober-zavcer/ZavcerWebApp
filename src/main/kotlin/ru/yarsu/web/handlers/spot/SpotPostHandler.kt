package ru.yarsu.web.handlers.spot

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.format.Jackson.auto
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.models.telegram.service.TelegramService
import ru.yarsu.web.handlers.teacher.FormData

//data class FormData(
//    val date: String,
//    val startTime: String,
//    val endTime: String,
//    val userId: Int
//
//)

class SpotPostHandler(
    private val databaseController: DatabaseController
) : HttpHandler {

//    private val formDataLens = Body.auto<FormData>().toLens()

    override fun invoke(request: Request): Response {
//        val formData = try {
//            formDataLens(request)
//        } catch (e: Exception) {
//            return Response(BAD_REQUEST).body("Неверный формат данных: ${e.message}")
//        }

        val user =
            UserModelLens(request)
                ?: return Response(UNAUTHORIZED).body("Пользователь не авторизован")

        val spotId =
            request.path("id")?.toIntOrNull()
                ?: return Response(BAD_REQUEST).body("Некорректный ID")

        val spot =
            databaseController.getSpotById(spotId)
                ?: return Response(NOT_FOUND).body("Репетиционная точка не найдена")

        val ownerIds = spot.owners

        // Получаем список пользователей по ID
        val owners =
            ownerIds.mapNotNull { ownerId ->
                databaseController.getUserById(ownerId)
            }

        // Берём список tg_id владельцев, фильтруя невалидные (<= 0)
        val ownerTgIds = owners.mapNotNull { it.tg_id.takeIf { id -> id > 0L } }

        TelegramService.spotOwnerNotification(ownerTgIds, spotId.toLong(), user.tg_id)
        TelegramService.studentSpotNotification(ownerTgIds, spotId.toLong(), user.tg_id)

        return Response(FOUND).header("Location", "/spots")
    }
}