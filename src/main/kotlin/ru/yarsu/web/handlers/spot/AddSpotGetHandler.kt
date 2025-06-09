package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.lens.MultipartForm
import org.http4k.lens.MultipartFormField
import ru.yarsu.web.domain.article.Spot
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.models.spot.AddSpotVM
import ru.yarsu.web.templates.ContextAwareViewRender

class AddSpotGetHandler(
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val addressLens = MultipartFormField.string().required("address")
    private val priceLens = MultipartFormField.string().required("price")
    private val hasDrumsLens = MultipartFormField.string().optional("hasDrums")
    private val guitarAmpsLens = MultipartFormField.string().optional("guitarAmps")
    private val bassAmpsLens = MultipartFormField.string().optional("bassAmps")
    private val districtLens = MultipartFormField.string().optional("district")

    override fun invoke(request: Request): Response {
        val spot =
            Spot(
                id = 0,
                name = "",
                price = 0,
                hasDrums = false,
                guitarAmps = 0,
                bassAmps = 0,
                description = "",
                address = "",
                district = DistrictEnums.UNKNOWN,
                images = listOf("defaultStudio.jpg"),
                twoWeekOccupation = emptyList(),
                owners = emptyList(),
            )

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

        val viewModel = AddSpotVM(spot, form)
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
