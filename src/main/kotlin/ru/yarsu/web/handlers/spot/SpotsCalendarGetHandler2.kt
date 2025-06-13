package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import ru.yarsu.web.models.spot.SpotsCalendar2VM
import ru.yarsu.web.templates.ContextAwareViewRender

class SpotsCalendarGetHandler2(private val htmlView: ContextAwareViewRender,): HttpHandler {
    override fun invoke(request: Request): Response {


        val viewModel = SpotsCalendar2VM()
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}