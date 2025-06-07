package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.core.body.form
import ru.yarsu.db.SpotData
import ru.yarsu.web.context.UserModelLens

class DeleteSpotPostHandler(
    private val spots: SpotData,
) : HttpHandler {
    private fun utf8Text(
        status: Status,
        text: String,
    ): Response = Response(status).header("Content-Type", "text/plain; charset=UTF-8").body(text)

    override fun invoke(request: Request): Response {
        val user =
            UserModelLens(request)
                ?: return utf8Text(Status.UNAUTHORIZED, "Пользователь не авторизован")

        val spotId =
            request.form("spotId")?.toIntOrNull()
                ?: return utf8Text(Status.BAD_REQUEST, "Некорректный ID точки")

        val existingSpot =
            spots.getById(spotId)
                ?: return utf8Text(Status.NOT_FOUND, "Точка не найдена")

        if (user.id !in existingSpot.owners) {
            return utf8Text(Status.FORBIDDEN, "Вы не можете удалить эту точку")
        }

        spots.deleteById(spotId)

        return Response(Status.FOUND).header("Location", "/spots")
    }
}
