package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.SpotData
import ru.yarsu.web.models.spot.EditSpotVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditSpotGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val spotData: SpotData,
) : HttpHandler {
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val addressLens = MultipartFormField.string().required("address")
    private val priceLens = MultipartFormField.string().required("price")
    private val hasDrumsLens = MultipartFormField.string().optional("hasDrums")
    private val guitarAmpsLens = MultipartFormField.string().optional("guitarAmps")
    private val bassAmpsLens = MultipartFormField.string().optional("bassAmps")
    private val districtLens = MultipartFormField.string().required("district")

    override fun invoke(request: Request): Response {
        val id =
            request.path("id")?.toIntOrNull()
                ?: return Response(NOT_FOUND).body("Неверный ID")

        val spot =
            spotData.getById(id)
                ?: return Response(NOT_FOUND).body("Спот не найден")

        val form =
            MultipartForm().with(
                nameLens of spot.name,
                descriptionLens of spot.description,
                addressLens of spot.address,
                priceLens of spot.price.toString(),
                hasDrumsLens of spot.hasDrums.toString(),
                guitarAmpsLens of spot.guitarAmps.toString(),
                bassAmpsLens of spot.bassAmps.toString(),
                districtLens of spot.district.name,
            )

        val viewModel = EditSpotVM(spot, form)
        return Response(OK).with(htmlView(request) of viewModel)
    }
}
