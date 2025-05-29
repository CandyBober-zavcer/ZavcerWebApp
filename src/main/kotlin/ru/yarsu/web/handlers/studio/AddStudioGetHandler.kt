package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.lens.MultipartForm
import org.http4k.lens.MultipartFormField
import ru.yarsu.web.domain.article.Location
import ru.yarsu.web.domain.article.PersonRole
import ru.yarsu.web.domain.article.Schedule
import ru.yarsu.web.domain.article.Studio
import ru.yarsu.web.models.studio.AddStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender

class AddStudioGetHandler(
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")

    override fun invoke(request: Request): Response {
        val entity =
            Studio(
                id = 0L,
                name = "",
                description = "",
                avatarFileName = listOf("defaultStudio.jpg"),
                roles = PersonRole(emptyList(), emptyList()),
                location = Location(""),
                capacity = 0,
                areaSquareMeters = 0.0,
                pricePerHour = 0.0,
                minBookingTimeHours = 1.0,
                equipment = emptyList(),
                schedule = Schedule(emptyMap()),
            )

        val form =
            MultipartForm().with(
                nameLens of entity.name,
                descriptionLens of entity.description.toString(),
            )

        val viewModel = AddStudioVM(entity, form)

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
