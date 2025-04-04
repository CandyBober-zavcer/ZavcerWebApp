package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import java.io.File

class AppConfig(
    val webConfig: WebConfig,
)

private fun getAppEnv(config: Config): Environment {
    return Environment.from(File("data/app.properties")) overrides
            Environment.JVM_PROPERTIES overrides
            Environment.ENV overrides
            config.defaultEnv
}

fun readConfigurations(): AppConfig {
    return AppConfig(
        WebConfig.createWebConfig(getAppEnv(WebConfig)),
    )
}
