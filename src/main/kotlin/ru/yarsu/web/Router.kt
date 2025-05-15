package ru.yarsu.web

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.config.AppConfig
import ru.yarsu.web.domain.models.telegram.JsonLogger
import ru.yarsu.web.handlers.PebbleHandler
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.handlers.home.HomePageHandler
import ru.yarsu.web.handlers.studio.StudioGetHandler
import ru.yarsu.web.handlers.studio.StudioPostHandler
import ru.yarsu.web.handlers.studio.StudiosGetHandler
import ru.yarsu.web.handlers.teacher.TeacherGetHandler
import ru.yarsu.web.handlers.teacher.TeacherPostHandler
import ru.yarsu.web.handlers.teacher.TeachersGetHandler
import ru.yarsu.web.handlers.telegramAuth.TelegramAuthGetHandler
import ru.yarsu.web.handlers.telegramAuth.TelegramAuthPostHandler
import ru.yarsu.web.templates.ContextAwareViewRender

class TestErrorHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        throw RuntimeException("Искусственная ошибка 500 для теста.")
    }
}

fun router(htmlView: ContextAwareViewRender, config: AppConfig): RoutingHttpHandler {
    return routes(
        "/" bind Method.GET to PebbleHandler(),
        "/test-error" bind Method.GET to TestErrorHandler(), // Такого Хендлера нет
        "/ping" bind Method.GET to PingHandler(),
        "/auth/telegram" bind Method.GET to TelegramAuthGetHandler(htmlView),
        "/auth/telegram" bind Method.POST to TelegramAuthPostHandler(
            jsonLogger = JsonLogger(config.telegramConfig.userDataFile),
            botToken = config.telegramConfig.botToken
        ),
        "/studio/{id}" bind Method.GET to StudioGetHandler(htmlView),
        "/studio" bind Method.POST to StudioPostHandler(config.telegramConfig.botToken, 1831874252.toString()),
        "/studios" bind Method.GET to StudiosGetHandler(htmlView),
        "/home" bind Method.GET to HomePageHandler(htmlView),
//        "/teacher" bind Method.GET to TeacherGetHandler(htmlView),
//        "/teacher" bind Method.POST to TeacherPostHandler(config.telegramConfig.botToken, 1831874252.toString()),
        "/teachers" bind Method.GET to TeachersGetHandler(htmlView),
        "/teacher/{id}" bind Method.GET to TeacherGetHandler(htmlView)
    )
}
