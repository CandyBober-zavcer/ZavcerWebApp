package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.routing.path
import ru.yarsu.db.ProfilesData
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle
import ru.yarsu.web.models.profile.EditProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditProfileGetHandler(private val htmlView: ContextAwareViewRender, private val profiles: ProfilesData) : HttpHandler {

    override fun invoke(request: Request): Response {
        val profileId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID профиля")
        val profile = profiles.getProfileById(profileId)
            ?: return Response(Status.NOT_FOUND).body("Профиль не найден")
        val allStyles = MusicStyle.entries
        val allInstruments = Instrument.entries

        val viewModel = EditProfileVM(
            profile,
            allInstruments,
            allStyles
        )
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}