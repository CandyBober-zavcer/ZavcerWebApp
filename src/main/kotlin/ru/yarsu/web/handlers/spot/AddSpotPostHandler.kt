package ru.yarsu.web.handlers.spot

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.lens.*
import ru.yarsu.db.SpotData
import ru.yarsu.web.context.UserModelLens
import ru.yarsu.web.domain.article.Spot
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.spot.AddSpotVM
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.utils.ImageUtils.generateSafePngFilename
import ru.yarsu.web.utils.ImageUtils.saveImageAsPng
import java.nio.file.Files
import java.nio.file.Paths

class AddSpotPostHandler(
    private val htmlView: ContextAwareViewRender,
    private val spots: SpotData,
) : HttpHandler {
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val addressLens = MultipartFormField.string().required("address")
    private val priceLens = MultipartFormField.string().required("price")
    private val hasDrumsLens = MultipartFormField.string().optional("hasDrums")
    private val guitarAmpsLens = MultipartFormField.string().optional("guitarAmps")
    private val bassAmpsLens = MultipartFormField.string().optional("bassAmps")
    private val districtLens = MultipartFormField.string().required("district")
    private val imageLens = MultipartFormFile.multi.required("photo")

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
        val user =
            UserModelLens(request)
                ?: return Response(UNAUTHORIZED).body("Пользователь не авторизован")

        val form = formLens(request)
        val errors = form.errors.map { it.meta.name }

        val newId = spots.getNextId()

        val name = lensOrDefault(nameLens, form) { "" }
        val description = lensOrDefault(descriptionLens, form) { "" }
        val address = lensOrDefault(addressLens, form) { "" }
        val price = lensOrDefault(priceLens, form) { "0" }.toIntOrNull()
        val hasDrums = hasDrumsLens(form) != null
        val guitarAmps = lensOrDefault(guitarAmpsLens, form) { "0" }?.toIntOrNull() ?: 0
        val bassAmps = lensOrDefault(bassAmpsLens, form) { "0" }?.toIntOrNull() ?: 0
        val district = DistrictEnums.entries.find { it.name == districtLens(form) } ?: DistrictEnums.UNKNOWN

        // Попробуем получить изображения (или пустой список при ошибке)
        val images =
            try {
                imageLens(form)
            } catch (e: LensFailure) {
                emptyList()
            }

        // Сохраняем изображения
        val savedImages = mutableListOf<String>()
        for ((index, image) in images.withIndex()) {
            if (image.content.available() > 0) {
                try {
                    val filename = generateSafePngFilename("spot_${newId}_$index", newId)
                    val path = Paths.get("public/image").resolve(filename)
                    Files.createDirectories(path.parent)
                    saveImageAsPng(image.content, path.toString())
                    savedImages.add(filename)
                } catch (e: Exception) {
                    println("Ошибка при сохранении изображения: ${e.message}")
                }
            }
        }

        val imageList = savedImages.ifEmpty { emptyList() }

        // Если есть ошибки или price некорректный — показываем форму с ошибками
        if (errors.isNotEmpty() || price == null) {
            val spot =
                Spot(
                    id = 0,
                    name = name,
                    price = price ?: 0,
                    hasDrums = hasDrums,
                    guitarAmps = guitarAmps,
                    bassAmps = bassAmps,
                    description = description,
                    address = address,
                    district = district,
                    images = imageList,
                    twoWeekOccupation = emptyList(),
                    owners = listOf(user.id),
                )
            val viewModel = AddSpotVM(spot, form)
            return Response(OK).with(htmlView(request) of viewModel)
        }

        // Создание и сохранение объекта Spot
        val spot =
            Spot(
                id = newId,
                name = name,
                price = price,
                hasDrums = hasDrums,
                guitarAmps = guitarAmps,
                bassAmps = bassAmps,
                description = description,
                address = address,
                district = district,
                images = imageList,
                twoWeekOccupation = emptyList(),
                owners = listOf(user.id),
            )

        spots.add(spot)

        return Response(FOUND).header("Location", "/spots")
    }
}
