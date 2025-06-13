package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.profile.EditProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.utils.ImageUtils.generateSafePngFilename
import ru.yarsu.web.utils.ImageUtils.saveImageAsPng
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class EditProfilePostHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val abilityLens = MultipartFormField.multi.optional("abilities")
    private val imageLens = MultipartFormFile.optional("avatar")

    private val formLens =
        Body
            .multipartForm(
                Validator.Feedback,
                nameLens,
                descriptionLens,
                abilityLens,
                imageLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(BAD_REQUEST).body("Некорректный ID профиля")

        val existingUser =
            databaseController.getUserById(userId)
                ?: return Response(NOT_FOUND).body("Профиль не найден")

        val allAbility = AbilityEnums.entries
        val form = formLens(request)

        val errors = form.errors.map { it.meta.name }
        if (errors.isNotEmpty()) {
            val viewModel =
                EditProfileVM(
                    user = existingUser,
                    allAbility = allAbility,
                    form = form,
                )
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val name = lensOrDefault(nameLens, form) { existingUser.name }
        val description = lensOrDefault(descriptionLens, form) { existingUser.description }

        val updatedImages = existingUser.images.toMutableList()

        val newAvatar = imageLens(form)
        if (newAvatar != null) {
            val bytes = newAvatar.content.readAllBytes()
            if (bytes.isNotEmpty()) {
                val safeFilename = generateSafePngFilename("user", userId)
                val avatarPath = Paths.get("public/image").resolve(safeFilename)
                Files.createDirectories(avatarPath.parent)
                try {
                    saveImageAsPng(bytes.inputStream(), avatarPath.toString())
                    updatedImages.add(0, safeFilename)
                } catch (e: Exception) {
                    println("Ошибка при сохранении Png: ${e.message}")
                }
            }
        }

        val updatedUser =
            existingUser.copy(
                name = name,
                description = description,
                images = updatedImages,
            )

        databaseController.updateUserInfo(userId, updatedUser)
        return Response(FOUND).header("Location", "/profile/$userId")
    }
}
