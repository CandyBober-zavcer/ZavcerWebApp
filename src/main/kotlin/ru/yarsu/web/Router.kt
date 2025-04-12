package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.web.handlers.PebbleHandler
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.handlers.home.HomePageHandler
import ru.yarsu.web.handlers.studio.StudioHandler
import ru.yarsu.web.handlers.studio.StudiosHandler
import ru.yarsu.web.handlers.teacher.TeacherHandler
import ru.yarsu.web.handlers.teacher.TeachersHandler
import ru.yarsu.web.handlers.telegramAuth.TelegramAuthHandler
import ru.yarsu.web.templates.ContextAwareViewRender

fun router(htmlView: ContextAwareViewRender): RoutingHttpHandler {
    return routes(
        "/" bind Method.GET to PebbleHandler(),
        "/ping" bind Method.GET to PingHandler(),
        "/auth/telegram" bind Method.GET to TelegramAuthHandler(htmlView),
        "/studio" bind Method.GET to StudioHandler(htmlView),
        "/studios" bind Method.GET to StudiosHandler(htmlView),
        "/home" bind Method.GET to HomePageHandler(htmlView),
        "/teacher" bind Method.GET to TeacherHandler(htmlView),
        "/teachers" bind Method.GET to TeachersHandler(htmlView),
    )
}
