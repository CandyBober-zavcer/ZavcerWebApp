package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.models.spot.EditSpotVM
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.utils.ImageUtils.generateSafePngFilename
import ru.yarsu.web.utils.ImageUtils.saveImageAsPng
import java.nio.file.Files
import java.nio.file.Paths

class EditSpotPostHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val addressLens = MultipartFormField.string().required("address")
    private val priceLens = MultipartFormField.string().required("price")
    private val hasDrumsLens = MultipartFormField.string().optional("hasDrums")
    private val guitarAmpsLens = MultipartFormField.string().optional("guitarAmps")
    private val bassAmpsLens = MultipartFormField.string().optional("bassAmps")
    private val districtLens = MultipartFormField.string().required("district")
    private val imageLens = MultipartFormFile.multi.required("photos")

    private val formLens =
        Body
            .multipartForm(
                Validator.Feedback,
                nameLens,
                descriptionLens,
                addressLens,
                priceLens,
                hasDrumsLens,
                guitarAmpsLens,
                bassAmpsLens,
                districtLens,
                imageLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val id =
            request.path("id")?.toIntOrNull()
                ?: return Response(NOT_FOUND).body("Неверный ID")

        val existingSpot =
            databaseController.getSpotById(id)
                ?: return Response(NOT_FOUND).body("Спот не найден")

        val form = formLens(request)

        if (form.errors.isNotEmpty()) {
            val viewModel = EditSpotVM(existingSpot, form)
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val name = nameLens(form)
        val description = descriptionLens(form)
        val address = addressLens(form)
        val price = priceLens(form).toIntOrNull() ?: return Response(BAD_REQUEST).body("Некорректная цена")
        val hasDrums = hasDrumsLens(form) != null
        val guitarAmps = guitarAmpsLens(form)?.toIntOrNull() ?: 0
        val bassAmps = bassAmpsLens(form)?.toIntOrNull() ?: 0
        val district = DistrictEnums.entries.find { it.name == districtLens(form) } ?: DistrictEnums.UNKNOWN

        val images =
            try {
                imageLens(form)
            } catch (e: LensFailure) {
                emptyList()
            }

        val savedImages = mutableListOf<String>()
        for ((index, image) in images.withIndex()) {
            if (image.content.available() > 0) {
                try {
                    val filename = generateSafePngFilename("spot_${id}_$index", id)
                    val path = Paths.get("public/image").resolve(filename)
                    Files.createDirectories(path.parent)
                    saveImageAsPng(image.content, path.toString())
                    savedImages.add(filename)
                } catch (e: Exception) {
                    println("Ошибка при сохранении изображения: ${e.message}")
                }
            }
        }

        val imageList =
            if (savedImages.isEmpty()) {
                existingSpot.images
            } else {
                savedImages
            }

        val updatedSpot =
            existingSpot.copy(
                name = name,
                price = price,
                hasDrums = hasDrums,
                guitarAmps = guitarAmps,
                bassAmps = bassAmps,
                description = description,
                address = address,
                district = district,
                images = imageList,
            )

        databaseController.updateSpotInfo(id, updatedSpot)

        return Response(FOUND).header("Location", "/spots")
    }
}
