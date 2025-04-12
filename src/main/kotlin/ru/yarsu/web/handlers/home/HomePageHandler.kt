package ru.yarsu.web.handlers.home

import org.http4k.core.*
import ru.yarsu.web.models.home.HomePageVM
import ru.yarsu.web.templates.ContextAwareViewRender

class HomePageHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = HomePageVM("Hello there!")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}