package ru.yarsu.config

import org.http4k.cloudnative.env.Environment

interface Config {
    val defaultEnv: Environment
}
