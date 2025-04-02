package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.web.handlers.PebbleHandler
import ru.yarsu.web.handlers.PingHandler

val router = routes(
    "/ping" bind Method.GET to PingHandler(),
    "/templates/pebble" bind Method.GET to PebbleHandler(),
)
