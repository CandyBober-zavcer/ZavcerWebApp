package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.teacher.EditTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.utils.ImageUtils.generateSafePngFilename
import ru.yarsu.web.utils.ImageUtils.saveImageAsPng
import java.nio.file.Files
import java.nio.file.Paths

class EditTeacherPostHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    private val pathLens = Path.long().of("id")
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val imageLens = MultipartFormFile.optional("photo")
    private val addressLens = MultipartFormField.string().required("address")
    private val districtLens = MultipartFormField.string().required("district")
    private val experienceLens = MultipartFormField.string().required("experience")
    private val phoneLens = MultipartFormField.string().required("phone")
    private val priceLens = MultipartFormField.string().required("price")

    private val formLens =
        Body
            .multipartForm(
                Validator.Feedback,
                nameLens,
                descriptionLens,
                imageLens,
                addressLens,
                districtLens,
                experienceLens,
                phoneLens,
                priceLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val teacherId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Неверный ID преподавателя")

        val existingTeacher =
            databaseController.getTeacherById(teacherId)
                ?: return Response(NOT_FOUND).body("Преподаватель не найден")

        val form = formLens(request)
        val errors = form.errors.map { it.meta.name }
        val allAbility = AbilityEnums.entries

        if (errors.isNotEmpty()) {
            val viewModel =
                EditTeacherVM(
                    teacher = existingTeacher,
                    ability = allAbility,
                    form = form,
                )
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val name = lensOrDefault(nameLens, form) { existingTeacher.name }
        val description = lensOrDefault(descriptionLens, form) { existingTeacher.description }
        val address = lensOrDefault(addressLens, form) { existingTeacher.address }
        val experience = lensOrDefault(experienceLens, form) { existingTeacher.experience.toString() }.toInt()
        val phone = lensOrDefault(phoneLens, form) { existingTeacher.phone }
        val price = lensOrDefault(priceLens, form) { existingTeacher.price.toString() }.toInt()
        val district = DistrictEnums.entries.find { it.name == districtLens(form) } ?: DistrictEnums.UNKNOWN

        val updatedImages = existingTeacher.images.toMutableList()

        val newPhoto = imageLens(form)
        if (newPhoto != null && newPhoto.content.available() > 0) {
            val safeFilename = generateSafePngFilename("user", teacherId)

            val avatarPath = Paths.get("public/image").resolve(safeFilename)

            Files.createDirectories(avatarPath.parent)

            try {
                saveImageAsPng(newPhoto.content, avatarPath.toString())
                updatedImages.add(0, safeFilename)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val updatedTeacher =
            existingTeacher.copy(
                name = name,
                phone = phone,
                experience = experience,
                abilities = existingTeacher.abilities,
                price = price,
                description = description,
                address = address,
                district = district,
                images = updatedImages,
                twoWeekOccupation = existingTeacher.twoWeekOccupation,
            )

        databaseController.updateUserInfo(teacherId, updatedTeacher)
        return Response(FOUND).header("Location", "/teacher/$teacherId")
    }
}
