package ru.yarsu.web.context

import org.http4k.core.RequestContexts
import org.http4k.lens.RequestContextKey
import ru.yarsu.web.domain.models.telegram.TelegramUser

val contexts = RequestContexts()
val TelegramUserLens = RequestContextKey.optional<TelegramUser>(contexts, "telegramUser")
