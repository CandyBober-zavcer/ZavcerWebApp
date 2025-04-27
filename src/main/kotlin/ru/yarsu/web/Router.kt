package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.web.handlers.PebbleHandler
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.handlers.home.HomePageHandler
import ru.yarsu.web.handlers.studio.StudioGetHandler
import ru.yarsu.web.handlers.studio.StudiosGetHandler
import ru.yarsu.web.handlers.teacher.TeacherGetHandler
import ru.yarsu.web.handlers.teacher.TeachersGetHandler
import ru.yarsu.web.handlers.telegramAuth.TelegramAuthGetHandler
import ru.yarsu.web.handlers.telegramAuth.TelegramAuthPostHandler
import ru.yarsu.web.templates.ContextAwareViewRender

fun router(htmlView: ContextAwareViewRender): RoutingHttpHandler {
    return routes(
        "/" bind Method.GET to PebbleHandler(),
        "/ping" bind Method.GET to PingHandler(),
        "/auth/telegram" bind Method.GET to TelegramAuthGetHandler(htmlView),
        "/auth/telegram" bind Method.POST to TelegramAuthPostHandler(),
        "/studio" bind Method.GET to StudioGetHandler(htmlView),
        "/studios" bind Method.GET to StudiosGetHandler(htmlView),
        "/home" bind Method.GET to HomePageHandler(htmlView),
        "/teacher" bind Method.GET to TeacherGetHandler(htmlView),
        "/teachers" bind Method.GET to TeachersGetHandler(htmlView),
    )
}
