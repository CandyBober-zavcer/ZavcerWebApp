package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import ru.yarsu.db.SpotData
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.enums.RoleEnums

fun spotOwnerOrAdminFilter(
    userLens: RequestContextLens<UserModel?>,
    spots: SpotData,
    spotIdPathParam: String = "id",
): Filter =
    Filter { next ->
        { request ->
            val currentUser = userLens(request)
            val spotId = Path.of(spotIdPathParam)(request).toIntOrNull()
            val targetSpot = spotId?.let { spots.getById(it) }

            when {
                currentUser == null ->
                    Response(Status.UNAUTHORIZED)
                        .body("Authentication required to access this resource")

                hasAdminPrivileges(currentUser) ->
                    next(request)

                targetSpot == null ->
                    Response(Status.NOT_FOUND)
                        .body("Rehearsal spot not found")

                currentUser.id in targetSpot.owners ->
                    next(request)

                else ->
                    Response(Status.FORBIDDEN)
                        .body("Access denied: You must be an admin or spot owner")
            }
        }
    }

private fun hasAdminPrivileges(user: UserModel): Boolean = user.roles.contains(RoleEnums.ADMIN) || user.roles.contains(RoleEnums.DIRECTOR)
