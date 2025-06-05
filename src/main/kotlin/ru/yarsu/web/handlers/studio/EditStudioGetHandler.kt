package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.StudiosData
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.studio.EditStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditStudioGetHandler(private val htmlView: ContextAwareViewRender, private val studios: StudiosData) :
    HttpHandler {

    private val pathLens = Path.long().of("id")

    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")

    private val formLens = Body.multipartForm(
        Validator.Feedback,
        nameLens,
        descriptionLens,
    ).toLens()

    override fun invoke(request: Request): Response {
        val studioId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID студии")

        val studio = studios.getStudioById(studioId)
            ?: return Response(Status.NOT_FOUND).body("Студия не найдена")
        val allInstruments = Instrument.entries

        val filledForm = MultipartForm()
            .with(nameLens of studio.name)
            .with(descriptionLens of studio.description.toString())

        val viewModel =
            EditStudioVM(
                studio,
                allInstruments,
                filledForm
            )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}