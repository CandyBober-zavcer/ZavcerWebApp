package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.string

class TelegramConfig(env: Environment) {
    val botToken: String = BOT_TOKEN(env)
    val userDataFile: String = USER_DATA_FILE(env)

    companion object {
        private val BOT_TOKEN = EnvironmentKey.string()
            .required("telegram.bot_token", "Telegram bot token required")

        private val USER_DATA_FILE = EnvironmentKey.string()
            .defaulted("telegram.user_data_file", "data/telegram_users.json",
                "Path to user data storage")

        operator fun invoke(env: Environment): TelegramConfig = TelegramConfig(env)
    }
}