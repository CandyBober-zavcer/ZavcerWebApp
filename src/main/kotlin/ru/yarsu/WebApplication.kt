package ru.yarsu

import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.jetbrains.exposed.sql.Database
import ru.yarsu.db.DataBaseController
import ru.yarsu.web.router

fun main() {
    Database.connect("jdbc:mysql://localhost/test", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "root")
    DataBaseController().init()
    val appWithStaticResources =
        routes(
            router,
            static(ResourceLoader.Classpath("/ru/yarsu/public")),
        )

    val server = appWithStaticResources.asServer(Netty(9000)).start()

    println("Server started on http://localhost:" + server.port())
    println("Press enter to exit application.")
    readln()
    server.stop()
}
