package ru.yarsu.web.models.error

import org.http4k.template.ViewModel

data class ServerErrorVM(val code: String, val message: String): ViewModel