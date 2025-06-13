package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.enums.RoleEnums

fun onlyAdminFilter(userLens: RequestContextLens<UserModel?>): Filter =
    Filter { next ->
        { request ->
            val user = userLens(request)
            when {
                user == null ->
                    Response(Status.UNAUTHORIZED)
                        .body("Please sign in to access admin features")

                !user.roles.contains(RoleEnums.ADMIN) ->
                    Response(Status.FORBIDDEN)
                        .body("Administrator privileges required")

                else -> next(request)
            }
        }
    }
