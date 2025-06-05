package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int
import org.http4k.lens.string

class WebConfig(env: Environment) {
    val port: Int = PORT(env)
    val authSalt: String = AUTH_SALT(env)
    val smtpUsername: String = SMTP_USERNAME(env)
    val smtpPassword: String = SMTP_PASSWORD(env)

    companion object {
        private val PORT = EnvironmentKey.int()
            .defaulted("web.port", 8080, "HTTP server port")

        private val AUTH_SALT = EnvironmentKey.string()
            .required("auth.salt", "Secret key for HMAC signing")

        private val SMTP_USERNAME = EnvironmentKey.string()
            .required("smtp.username", "Login email")

        private val SMTP_PASSWORD = EnvironmentKey.string()
            .required("smtp.password", "Password from email")
        operator fun invoke(env: Environment): WebConfig = WebConfig(env)
    }
}