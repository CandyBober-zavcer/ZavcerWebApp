package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import ru.yarsu.db.StudiosData
import ru.yarsu.web.domain.article.Location
import ru.yarsu.web.domain.article.PersonRole
import ru.yarsu.web.domain.article.Schedule
import ru.yarsu.web.domain.article.Studio
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.studio.AddStudioVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class AddStudioPostHandler(
    private val htmlView: ContextAwareViewRender,
    private val studios: StudiosData
) : HttpHandler {

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
        val form = formLens(request)
        val errors = form.errors.map { it.meta.name }.toList()

        val name = lensOrDefault(nameLens, form) { "" }
        val description = lensOrDefault(descriptionLens, form) { "" }
        val address = lensOrDefault(addressLens, form) { "" }
        val capacity = lensOrDefault(capacityLens, form) { "1" }.toInt()
        val area = lensOrDefault(areaSquareMetersLens, form) { "0" }.toDouble()
        val price = lensOrDefault(priceLens, form) { "0" }.toDouble()
        val minBookingTime = lensOrDefault(minBookingTimeLens, form) { "1" }.toDouble()

        // Подготовка ID и имени файла
        val newId = studios.getNextId() // тебе нужно добавить этот метод
        var avatarFileName = listOf<String>()

        form.use {
            val file = imageLens(it)
            if (file != null && file.content.available() > 0) {
                val originalName = file.filename ?: "studio_$newId.png"
                val extension = originalName.substringAfterLast('.', "png")
                val safeFilename = "studio_$newId.$extension".replace(Regex("[^a-zA-Z0-9._-]"), "_")
                val avatarPath = Paths.get("src/main/resources/ru/yarsu/public/img").resolve(safeFilename)

                Files.createDirectories(avatarPath.parent)
                Files.copy(file.content, avatarPath, StandardCopyOption.REPLACE_EXISTING)

                println("Файл сохранён как: $safeFilename")
                avatarFileName = listOf(safeFilename)
            } else {
                println("Файл не был загружен или пуст.")
            }
        }

        val studio = Studio(
            id = newId,
            name = name,
            description = description,
            avatarFileName = avatarFileName,
            roles = PersonRole(listOf(1831874252), listOf(777990904)), // Заглушка
            location = Location(address),
            capacity = capacity,
            areaSquareMeters = area,
            pricePerHour = price,
            minBookingTimeHours = minBookingTime,
            equipment = emptyList(),
            schedule = Schedule(emptyMap()),
        )

        // Если есть ошибки — показать форму с ошибками
        if (errors.isNotEmpty()) {
            val viewModel = AddStudioVM(studio, form)
            return Response(OK).with(htmlView(request) of viewModel)
        }

        studios.addStudio(studio) // Добавляем студию

        return Response(FOUND).header("Location", "/studios")
    }
}
