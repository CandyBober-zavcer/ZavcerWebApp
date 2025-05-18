package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.StudiosData
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.studio.EditStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class EditStudioPostHandler(
    private val htmlView: ContextAwareViewRender,
    private val studios: StudiosData
) : HttpHandler {

    private val pathLens = Path.long().of("id")

    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val imageLens = MultipartFormFile.optional("photo")
    private val addressLens = MultipartFormField.string().required("address")
    private val capacityLens = MultipartFormField.string().required("capacity")
    private val areaSquareMetersLens = MultipartFormField.string().required("areaSquareMeters")
    private val priceLens = MultipartFormField.string().required("pricePerHour")
    private val minBookingTimeLens = MultipartFormField.string().required("minBookingTimeHours")

    private val formLens = Body.multipartForm(
        Validator.Feedback,
        nameLens,
        descriptionLens,
        imageLens,
        addressLens,
        capacityLens,
        areaSquareMetersLens,
        priceLens,
        minBookingTimeLens,
    ).toLens()

    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)
        val studioId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Неверный ID студии")

        val existingStudio = studios.getStudioById(studioId)
            ?: return Response(NOT_FOUND).body("Студия не найдена")

        val form = formLens(request)
        val errors = form.errors.map { it.meta.name }
        val allInstruments = Instrument.entries

        if (errors.isNotEmpty()) {
            val viewModel = EditStudioVM(
                studio = existingStudio,
                user = user?.id?.toString() ?: "null",
                allInstruments = allInstruments,
                form = form
            )
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val name = lensOrDefault(nameLens, form) { existingStudio.name }
        val description = lensOrDefault(descriptionLens, form) { existingStudio.description }
        val address = lensOrDefault(addressLens, form) { existingStudio.location.address }
        val capacity = lensOrDefault(capacityLens, form) { existingStudio.capacity.toString() }.toInt()
        val area = lensOrDefault(areaSquareMetersLens, form) {existingStudio.areaSquareMeters.toString()}.toDouble()
        val price = lensOrDefault(priceLens, form) {existingStudio.pricePerHour.toString()}.toDouble()
        val minBookingTime = lensOrDefault(minBookingTimeLens, form) {existingStudio.minBookingTimeHours.toString()}.toDouble()

        println("Имя: $name")
        println("Описание: $description")

        var avatarFileName = existingStudio.avatarFileName

        form.use {
            val file = imageLens(it)
            if (file != null && file.content.available() > 0) {
                val originalName = file.filename ?: "studio_${studioId}.png"
                val extension = originalName.substringAfterLast('.', "png")
                val filename = "studio_${studioId}.$extension"
                val safeFilename = filename.replace(Regex("[^a-zA-Z0-9._-]"), "_")
                val avatarPath = Paths.get("src/main/resources/ru/yarsu/public/img").resolve(safeFilename)

                Files.createDirectories(avatarPath.parent)
                Files.copy(file.content, avatarPath, StandardCopyOption.REPLACE_EXISTING)

                println("Файл обновлён как: $safeFilename")
                println("Путь до файла: ${avatarPath.toAbsolutePath()}")

                avatarFileName = listOf(safeFilename)
            } else {
                println("Файл не был загружен или пуст.")
            }
        }


        val updateStudio = Studio(
            id = studioId,
            name = name,
            description = description,
            avatarFileName = avatarFileName,
            roles = existingStudio.roles,
            location = Location(address),
            capacity = capacity,
            areaSquareMeters = area,
            pricePerHour = price,
            minBookingTimeHours = minBookingTime,
            equipment = existingStudio.equipment,
            schedule = existingStudio.schedule,
        )

        studios.updateStudio(studioId, updateStudio)
        return Response(FOUND).header("Location", "/studio/${studioId}")
    }
}
