package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Request
import ru.yarsu.web.context.TelegramUserLens
import ru.yarsu.web.domain.models.telegram.AuthUtils

val telegramUserFilter = Filter { next ->
    { request: Request ->
        val user = AuthUtils.getUserFromCookie(request)
        if (user != null) {
            TelegramUserLens[request] = user
        }
        next(request)
    }
}
