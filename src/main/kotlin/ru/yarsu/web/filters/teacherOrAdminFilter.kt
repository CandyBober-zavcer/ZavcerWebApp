package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.RoleEnums

fun teacherOrAdminFilter(
    userLens: RequestContextLens<User?>,
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

                hasAdminPrivileges(currentUser) ->
                    next(request)

                requestedId == null ->
                    Response(Status.BAD_REQUEST)
                        .body("Invalid resource identifier")

                isTeacherAccessingOwnData(currentUser, requestedId) ->
                    next(request)

                else ->
                    Response(Status.FORBIDDEN)
                        .body("Access denied. Requires admin privileges or teacher ownership")
            }
        }
    }

private fun hasAdminPrivileges(user: User): Boolean = user.roles.contains(RoleEnums.ADMIN) || user.roles.contains(RoleEnums.DIRECTOR)

private fun isTeacherAccessingOwnData(
    user: User,
    requestedId: Int,
): Boolean = user.roles.contains(RoleEnums.TEACHER) && user.id == requestedId