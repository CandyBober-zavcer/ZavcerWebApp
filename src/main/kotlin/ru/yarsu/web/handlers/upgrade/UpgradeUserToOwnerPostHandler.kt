package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.RoleEnums
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrDefaultAbilities
import ru.yarsu.web.models.upgrade.UpgradeUserToOwnerVM
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.utils.ImageUtils.generateSafePngFilename
import ru.yarsu.web.utils.ImageUtils.saveImageAsPng
import java.nio.file.Files
import java.nio.file.Paths

class UpgradeUserToOwnerPostHandler(
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
    private val abilitiesLens = MultipartFormField.multi.required("abilities[]")

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
                abilitiesLens
            ).toLens()

    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Неверный ID пользоватя")

        val existingUser =
            databaseController.getUserIfNotOwner(userId)
                ?: return Response(NOT_FOUND).body("Пользователь не найден")

        val form = formLens(request)
        val errors = form.errors.map { it.meta.name }
        val allAbility = AbilityEnums.entries

        val selectedAbilities =
            lensOrDefaultAbilities(abilitiesLens, form) {
                existingUser.abilities
            }

        if (errors.isNotEmpty()) {
            val viewModel =
                UpgradeUserToOwnerVM(
                    user = existingUser,
                    allAbility = allAbility,
                    form = form,
                )
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val name = lensOrDefault(nameLens, form) { existingUser.name }
        val description = lensOrDefault(descriptionLens, form) { existingUser.description }
        val address = lensOrDefault(addressLens, form) { existingUser.address }
        val experience = lensOrDefault(experienceLens, form) { existingUser.experience.toString() }.toInt()
        val phone = lensOrDefault(phoneLens, form) { existingUser.phone }
        val price = lensOrDefault(priceLens, form) { existingUser.price.toString() }.toInt()
        val district = DistrictEnums.entries.find { it.name == districtLens(form) } ?: DistrictEnums.UNKNOWN

        val updatedImages = existingUser.images.toMutableList()

        val newPhoto = imageLens(form)
        if (newPhoto != null && newPhoto.content.available() > 0) {
            val safeFilename = generateSafePngFilename("user", userId)
            val avatarPath = Paths.get("public/image").resolve(safeFilename)

            Files.createDirectories(avatarPath.parent)
            try {
                saveImageAsPng(newPhoto.content, avatarPath.toString())
                updatedImages.add(0, safeFilename)
            } catch (e: Exception) {
                println("Ошибка при сохранении Png: ${e.message}")
            }
        }

        val updatedOwner =
            existingUser.copy(
                name = name,
                phone = phone,
                experience = experience,
                abilities = selectedAbilities,
                price = price,
                description = description,
                address = address,
                district = district,
                images = updatedImages,
                roles = (existingUser.roles + RoleEnums.PENDING_OWNER).toMutableSet(),
            )

        databaseController.updateUserInfo(userId, updatedOwner)
        return Response(FOUND).header("Location", "/spots")
    }
}
