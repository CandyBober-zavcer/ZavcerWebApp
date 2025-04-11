package ru.yarsu.web.handlers.telegramAuth

import org.http4k.core.*
import ru.yarsu.web.models.telegramAuth.TelegramAuthVM
import ru.yarsu.web.templates.ContextAwareViewRender


class TelegramAuthHandler(private val htmlView: ContextAwareViewRender) : HttpHandler {

    override fun invoke(request: Request): Response {
        val viewModel = TelegramAuthVM("Hello there!")
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
