package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.enums.RoleEnums

fun directorOrAdminFilter(userLens: RequestContextLens<UserModel?>): Filter =
    Filter { next ->
        { request ->
            val user = userLens(request)
            when {
                user == null ->
                    Response(Status.UNAUTHORIZED)
                        .body("Authentication required. Please sign in.")

                !hasRequiredRole(user) ->
                    Response(Status.FORBIDDEN)
                        .body("Insufficient privileges. Requires ${RoleEnums.DIRECTOR} or ${RoleEnums.ADMIN} role.")

                else -> next(request)
            }
        }
    }

private fun hasRequiredRole(user: UserModel): Boolean = user.roles.contains(RoleEnums.DIRECTOR) || user.roles.contains(RoleEnums.ADMIN)
