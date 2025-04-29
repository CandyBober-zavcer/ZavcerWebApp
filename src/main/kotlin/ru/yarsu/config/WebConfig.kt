package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int

class WebConfig(env: Environment) {
    val port: Int = PORT(env)

    companion object {
        private val PORT = EnvironmentKey.int()
            .defaulted("web.port", 8080, "HTTP server port")

        operator fun invoke(env: Environment): WebConfig = WebConfig(env)
    }
}