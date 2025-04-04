package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.web.handlers.PebbleHandler
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.templates.ContextAwareViewRender

fun router(htmlView: ContextAwareViewRender): RoutingHttpHandler {
    return routes(
        "/" bind Method.GET to PingHandler()
    )
}
