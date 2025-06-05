package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import java.io.File

class AppConfig {
    val webConfig: WebConfig
    val telegramConfig: TelegramConfig
    val gmailConfig: GmailConfig

    init {
        val env = getAppEnv()
        webConfig = WebConfig(env)
        telegramConfig = TelegramConfig(env)
        gmailConfig = GmailConfig(env)
    }

    private fun getAppEnv(): Environment {
        return Environment.from(File("data/app.properties")) overrides
                Environment.ENV overrides
                Environment.JVM_PROPERTIES overrides
                Environment.EMPTY
    }
}