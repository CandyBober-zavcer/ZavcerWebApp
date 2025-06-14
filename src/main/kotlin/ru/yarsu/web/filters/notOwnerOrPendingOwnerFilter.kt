package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.RoleEnums

fun notOwnerOrPendingOwnerFilter(
    userLens: RequestContextLens<User?>,
    userIdPathParam: String = "id",
): Filter =
    Filter { next ->
        { request ->
            val user = userLens(request)
            val requestedUserId = Path.of(userIdPathParam)(request).toIntOrNull()

            when {
                user == null ->
                    Response(Status.UNAUTHORIZED)
                        .body("Authentication required to access this resource")

                requestedUserId == null ->
                    Response(Status.BAD_REQUEST)
                        .body("Invalid user ID format in request URL")

                isAllowedAccess(user, requestedUserId) ->
                    next(request)

                else ->
                    Response(Status.FORBIDDEN)
                        .body("You don't have permission to perform this action for the specified user")
            }
        }
    }

private fun isAllowedAccess(
    user: User,
    requestedUserId: Int,
): Boolean {
    val isAccessingOwnData = user.id == requestedUserId
    val hasRestrictedRole =
        user.roles.contains(RoleEnums.OWNER) ||
                user.roles.contains(RoleEnums.PENDING_OWNER)

    return isAccessingOwnData && !hasRestrictedRole
}