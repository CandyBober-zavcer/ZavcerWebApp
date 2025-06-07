package ru.yarsu.web.handlers.spot

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.db.SpotData
import ru.yarsu.web.models.spot.SpotsVM
import ru.yarsu.web.templates.ContextAwareViewRender

class SpotsListHandler(
    private val htmlView: ContextAwareViewRender,
    private val spots: SpotData,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val viewModel = SpotsVM(spots.getAll())
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
