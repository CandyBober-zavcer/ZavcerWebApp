package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.databasecontrollers.JsonController
import ru.yarsu.web.models.spot.SpotVM
import ru.yarsu.web.templates.ContextAwareViewRender

class SpotGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val id = request.path("id")?.toIntOrNull()
            ?: return Response(NOT_FOUND).body("Неверный ID")

        val spot = databaseController.getSpotById(id)
            ?: return Response(NOT_FOUND).body("Спот не найден")
        val userId = spot.owners[0]
        val phone = databaseController.getUserById(userId)?.phone
        println(phone)
        val viewModel = SpotVM(spot, JsonController.getAvailableDatesForSpotJson(id), phone)
        return Response(OK).with(htmlView(request) of viewModel)
    }
}
