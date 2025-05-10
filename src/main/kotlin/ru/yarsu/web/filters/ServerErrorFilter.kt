package ru.yarsu.web.filters

import org.http4k.core.*
import ru.yarsu.web.models.error.ServerErrorVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ServerErrorFilter(private val htmlView: ContextAwareViewRender) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler = ServerErrorHandler(next, htmlView)
}

class ServerErrorHandler(
    private val next: HttpHandler,
    private val htmlView: ContextAwareViewRender
) : HttpHandler {
    override fun invoke(request: Request): Response {
        return try {
            next(request)
        } catch (e: Exception) {

            val statusCode = Status.INTERNAL_SERVER_ERROR
            val errorCode = statusCode.code.toString()

            Response(statusCode).with(
                htmlView(request) of ServerErrorVM(
                    code = errorCode,
                    message = "Внутренняя ошибка сервера. Пожалуйста, попробуйте позже."
                )
            )
        }
    }
}
