package ru.yarsu

import org.http4k.core.ContentType
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.web.rendererProvider
import ru.yarsu.web.router
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.config.readConfigurations

fun main() {
    val appConfig = readConfigurations()
    val renderer = rendererProvider(true)
    val htmlView = ContextAwareViewRender.withContentType(renderer, ContentType.TEXT_HTML)

    val appWithStaticResources = routes(
        routes(
            router(
                htmlView
            ),
            static(ResourceLoader.Classpath("/ru/yarsu/public")),
        ),
    )

    val server = appWithStaticResources.asServer(Netty(appConfig.webConfig.webPort)).start()

    println("Server started on http://localhost:" + server.port())
    println("Press enter to exit application.")
    readln()
    server.block()
}