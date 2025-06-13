package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.web.models.spot.SpotsCalendarVM
import ru.yarsu.web.templates.ContextAwareViewRender

class SpotsCalendarGetHandler(private val htmlView: ContextAwareViewRender,): HttpHandler {
    override fun invoke(request: Request): Response {


        val viewModel = SpotsCalendarVM()
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}