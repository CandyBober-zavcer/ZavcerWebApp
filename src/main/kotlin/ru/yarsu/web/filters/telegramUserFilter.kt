package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.with
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.models.telegram.AuthUtils

fun userAuthFilter(
    authSalt: String,
    databaseController: DatabaseController
): Filter =
    Filter { next ->
        { request ->
            val user = AuthUtils.getUserFromCookie(request, authSalt, databaseController)
            val updatedRequest =
                if (user != null) {
                    request.with(UserModelLens of user)
                } else {
                    request
                }
            next(updatedRequest)
        }
    }
