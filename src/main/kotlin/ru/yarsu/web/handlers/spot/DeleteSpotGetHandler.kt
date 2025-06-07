package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.SpotData
import ru.yarsu.web.models.spot.DeleteSpotVM
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteSpotGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val spots: SpotData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val spotId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID точки")

        val spot =
            spots.getById(spotId)
                ?: return Response(Status.NOT_FOUND).body("Точка не найдена")

        val viewModel = DeleteSpotVM(spot)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
