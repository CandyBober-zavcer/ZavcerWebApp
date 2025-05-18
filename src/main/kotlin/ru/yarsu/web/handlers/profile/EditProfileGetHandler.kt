package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.ProfilesData
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.profile.EditProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditProfileGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val profiles: ProfilesData
) : HttpHandler {

    private val pathLens = Path.long().of("id")
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val instrumentsLens = MultipartFormField.multi.required("instruments")
    private val stylesLens = MultipartFormField.multi.required("styles")

    private val formLens = Body.multipartForm(
        Validator.Feedback,
        nameLens,
        descriptionLens,
        instrumentsLens,
        stylesLens,
    ).toLens()

    override fun invoke(request: Request): Response {
        val profileId = request.path("id")?.toLongOrNull()
            ?: return Response(BAD_REQUEST).body("Некорректный ID профиля")

        val profile = profiles.getProfileById(profileId)
            ?: return Response(NOT_FOUND).body("Профиль не найден")

        val allStyles = MusicStyle.entries
        val allInstruments = Instrument.entries

        val filledForm = MultipartForm()
            .with(nameLens of profile.name)
            .with(descriptionLens of profile.description)


        val viewModel =
            EditProfileVM(
                profile,
                allInstruments,
                allStyles,
                filledForm
            )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
