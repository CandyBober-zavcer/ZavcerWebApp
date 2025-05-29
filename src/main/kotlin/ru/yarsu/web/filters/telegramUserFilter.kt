package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.with
import ru.yarsu.db.UserData
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.models.telegram.AuthUtils

fun telegramUserFilter(
    authSalt: String,
    users: UserData,
): Filter =
    Filter { next ->
        { request ->
            val user = AuthUtils.getUserFromCookie(request, authSalt, users)
            val requestWithUser =
                if (user != null) {
                    request.with(UserModelLens of user)
                } else {
                    request
                }
            next(requestWithUser)
        }
    }
