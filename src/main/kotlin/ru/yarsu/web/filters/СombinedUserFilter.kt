package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.UserData
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.article.SessionStorage
import ru.yarsu.web.domain.models.telegram.AuthUtils

fun combinedUserFilter(
    authSalt: String,
    databaseController: DatabaseController,
    sessionStorage: SessionStorage,
): Filter =
    Filter { next ->
        { request ->
            // пытаемся достать user из Tg
            val telegramUser = AuthUtils.getUserFromCookie(request, authSalt, databaseController)

            // пытаемся достать user из сессии
            val sessionToken = request.cookie("session")?.value
            val sessionUser = sessionToken?.let { sessionStorage.getUserId(it) }?.let { databaseController.getUserById(it) }

            // выбираем любого найденного (например, telegram > session, или наоборот)
            val finalUser = telegramUser ?: sessionUser

            val updatedRequest =
                if (finalUser != null) {
                    request.with(UserModelLens of finalUser)
                } else {
                    request
                }

            next(updatedRequest)
        }
    }
