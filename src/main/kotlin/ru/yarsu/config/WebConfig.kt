package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int

class WebConfig(
    val webPort: Int,
) {
    companion object : Config {
        private val webPortLens = EnvironmentKey.int().required("web.port", "Application web port")
        override val defaultEnv =
            Environment.defaults(
                webPortLens of 8080,
            )

        fun createWebConfig(env: Environment): WebConfig {
            return WebConfig(webPortLens(env))
        }
    }
}
