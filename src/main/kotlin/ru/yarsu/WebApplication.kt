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
import ru.yarsu.web.rendererProvider
import ru.yarsu.web.context.contexts
import ru.yarsu.web.context.TelegramUserLens
import ru.yarsu.web.router
import ru.yarsu.web.templates.ContextAwareViewRender
import org.jetbrains.exposed.sql.Database
import ru.yarsu.db.*
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.article.SessionStorage
import ru.yarsu.web.domain.models.email.EmailService
import ru.yarsu.web.filters.*

fun main() {
    val requestContextFilter = ServerFilters.InitialiseRequestContext(contexts)
    val appConfig = AppConfig()

    val renderer = rendererProvider(true)
    val htmlView = ContextAwareViewRender
        .withContentType(renderer, ContentType.TEXT_HTML)
        .associateContextLens("user", UserModelLens)

    val teachers = TeachersData()
    val studio = StudiosData()
    val users = UserData()
    val sessionStorage = SessionStorage()

    val app = requestContextFilter
        .then(combinedUserFilter(appConfig.webConfig.authSalt, users, sessionStorage))
        .then(NotFoundFilter(htmlView))
        .then(ServerErrorFilter(htmlView))
        .then(
            routes(
                router(renderer, htmlView, appConfig, teachers, studio, users, sessionStorage),
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
