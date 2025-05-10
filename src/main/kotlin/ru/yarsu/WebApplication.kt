package ru.yarsu

import org.http4k.core.ContentType
import org.http4k.core.then
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.config.AppConfig
import ru.yarsu.web.filters.NotFoundFilter
import ru.yarsu.web.filters.ServerErrorFilter
import ru.yarsu.web.rendererProvider
import ru.yarsu.web.router
import ru.yarsu.web.templates.ContextAwareViewRender

fun main() {
    val appConfig = AppConfig()

    val renderer = rendererProvider(true)
    val htmlView = ContextAwareViewRender.withContentType(renderer, ContentType.TEXT_HTML)

    val app = NotFoundFilter(htmlView).then(
        ServerErrorFilter(htmlView).then(
            routes(
                router(htmlView, appConfig),
                static(ResourceLoader.Classpath("/ru/yarsu/public"))
            )
        )
    )

    val server = app.asServer(Netty(appConfig.webConfig.port)).start()

    println("Server started on http://localhost:${server.port()}")
    println("Press enter to exit application.")
    readln()
    server.stop()
}
