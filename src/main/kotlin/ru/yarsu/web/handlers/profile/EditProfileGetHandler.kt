package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.tables.ProfilesData
import ru.yarsu.web.models.profile.EditProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditProfileGetHandler(private val htmlView: ContextAwareViewRender): HttpHandler {

    override fun invoke(request: Request): Response {
        val profileId = request.path("id")?.toIntOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID профиля")
        val profile = ProfilesData().fillProfiles().find { it.id.toInt() == profileId }
        if (profile == null) {
            return Response(Status.NOT_FOUND).body("Профиль не найден")
        }
        val viewModel = EditProfileVM(profile)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}