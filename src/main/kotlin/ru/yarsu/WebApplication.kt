package ru.yarsu

import org.http4k.core.ContentType
import org.http4k.core.Filter
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.config.AppConfig
import ru.yarsu.web.filters.NotFoundFilter
import ru.yarsu.web.filters.ServerErrorFilter
import ru.yarsu.web.rendererProvider
import ru.yarsu.web.context.contexts
import ru.yarsu.web.context.TelegramUserLens
import ru.yarsu.web.router
import ru.yarsu.web.templates.ContextAwareViewRender
import org.jetbrains.exposed.sql.Database
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.filters.telegramUserFilter

fun main() {
    val requestContextFilter = ServerFilters.InitialiseRequestContext(contexts)
    val appConfig = AppConfig()

    val renderer = rendererProvider(true)
    val htmlView = ContextAwareViewRender
        .withContentType(renderer, ContentType.TEXT_HTML)
        .associateContextLens("user", TelegramUserLens)



    val app = requestContextFilter
        .then(telegramUserFilter)
        .then(NotFoundFilter(htmlView))
        .then(ServerErrorFilter(htmlView))
        .then(
            routes(
                router(htmlView, appConfig),
                static(ResourceLoader.Classpath("/ru/yarsu/public"))
            )
        )

//    Database.connect("jdbc:mysql://localhost/test", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "root")
//    DataBaseController().init()
//    val appWithStaticResources =
//        routes(
//            router,
//            static(ResourceLoader.Classpath("/ru/yarsu/public")),
//        )

    val server = app.asServer(Netty(appConfig.webConfig.port)).start()

    println("Server started on http://localhost:${server.port()}")
    println("Press enter to exit application.")
    readln()
    server.stop()
}
