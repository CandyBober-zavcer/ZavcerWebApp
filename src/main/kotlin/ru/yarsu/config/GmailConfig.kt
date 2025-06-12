package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.string

class GmailConfig(
    env: Environment,
) {
    val clientId: String = CLIENT_ID(env)
    val clientSecret: String = CLIENT_SECRET(env)

    companion object {
        private val CLIENT_ID =
            EnvironmentKey
                .string()
                .required("gmail.client_id", "Gmail client id required")

        private val CLIENT_SECRET =
            EnvironmentKey
                .string()
                .required("gmail.client_secret", "Gmail client secret required")

        operator fun invoke(env: Environment): GmailConfig = GmailConfig(env)
    }
}
