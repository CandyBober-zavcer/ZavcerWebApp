package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.enums.RoleEnums

fun notTeacherFilter(
    userLens: RequestContextLens<UserModel?>,
    idPathParam: String = "id",
): Filter =
    Filter { next ->
        { request ->
            val currentUser = userLens(request)
            val requestedId = Path.of(idPathParam)(request).toIntOrNull()

            when {
                currentUser == null ->
                    Response(Status.UNAUTHORIZED)
                        .body("Please sign in to access this resource")

                requestedId == null ->
                    Response(Status.BAD_REQUEST)
                        .body("Invalid user ID format")

                canAccessResource(currentUser, requestedId) ->
                    next(request)

                else ->
                    Response(Status.FORBIDDEN)
                        .body("Access restricted: teachers cannot perform this action")
            }
        }
    }

private fun canAccessResource(
    user: UserModel,
    requestedId: Int,
): Boolean =
    user.id == requestedId &&
        !user.roles.contains(RoleEnums.TEACHER) &&
        !user.roles.contains(RoleEnums.PENDING_TEACHER)
