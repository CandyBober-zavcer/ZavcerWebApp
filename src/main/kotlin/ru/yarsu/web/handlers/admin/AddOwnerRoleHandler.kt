package ru.yarsu.web.handlers.admin

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.yarsu.db.UserData

class AddOwnerRoleHandler(private val users: UserData) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId = request.path("id")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        users.addOwnerRoleById(userId)
        return Response(Status.FOUND).header("Location", "/admin/user/$userId")
    }
}