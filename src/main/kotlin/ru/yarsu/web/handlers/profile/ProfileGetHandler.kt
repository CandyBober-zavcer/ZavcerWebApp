package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.ProfilesData
import ru.yarsu.web.models.profile.ProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ProfileGetHandler(private val htmlView: ContextAwareViewRender): HttpHandler {

    override fun invoke(request: Request): Response {
        val profileId = request.path("id")?.toIntOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID профиля")
        val profile = ProfilesData().getAllProfiles().find { it.id.toInt() == profileId }
        if (profile == null) {
            return Response(Status.NOT_FOUND).body("Профиль не найден")
        }
        val viewModel = ProfileVM(profile)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}