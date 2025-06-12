package ru.yarsu.web.models.error

import org.http4k.template.ViewModel

data class NotFoundVM(
    val code: String,
    val message: String,
) : ViewModel
