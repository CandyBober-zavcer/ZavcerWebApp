package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.article.SessionStorage

fun sessionUserFilter(
    sessionStorage: SessionStorage,
    databaseController: DatabaseController,
): Filter =
    Filter { next ->
        { request ->
            val sessionToken = request.cookie("session")?.value
            val userId = sessionToken?.let { sessionStorage.getUserId(it) }
            val user = userId?.let { databaseController.getUserById(it) }

            if (user != null) {
                next(request.with(UserModelLens of user))
            } else {
                next(request)
            }
        }
    }
