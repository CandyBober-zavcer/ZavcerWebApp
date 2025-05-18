package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.FOUND
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.ProfilesData
import ru.yarsu.web.domain.article.Profile
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.profile.EditProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.Paths

class EditProfilePostHandler(
    private val htmlView: ContextAwareViewRender,
    private val profiles: ProfilesData
) : HttpHandler {

    private val pathLens = Path.long().of("id")
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val imageLens = MultipartFormFile.optional("avatar")

    private val formLens = Body.multipartForm(
        Validator.Feedback,
        nameLens,
        descriptionLens,
        imageLens,
    ).toLens()

    override fun invoke(request: Request): Response {
        val profileId = request.path("id")?.toLongOrNull()
            ?: return Response(BAD_REQUEST).body("Некорректный ID профиля")

        val existingProfile = profiles.getProfileById(profileId)
            ?: return Response(NOT_FOUND).body("Профиль не найден")

        val allInstruments = Instrument.entries
        val allStyles = MusicStyle.entries

        val form = formLens(request)

        val errors = form.errors.map { it.meta.name }.toList()
        if (errors.isNotEmpty()) {
            val viewModel = EditProfileVM(
                profile = existingProfile,
                allInstruments = allInstruments,
                allStyles = allStyles,
                form = form
            )
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val name = lensOrDefault(nameLens, form) { existingProfile.name }
        val description = lensOrDefault(descriptionLens, form) { existingProfile.description }

        println("Имя: $name")
        println("Описание: $description")

        var avatarFileName = existingProfile.avatarFileName

        form.use {
            imageLens(it)?.let { file ->
                val originalName = file.filename ?: "avatar_${profileId}.png"
                val extension = originalName.substringAfterLast('.', "png")
                val filename = "avatar_${profileId}.$extension"
                val safeFilename = filename.replace(Regex("[^a-zA-Z0-9._-]"), "_")
                val avatarPath = Paths.get("src/main/resources/ru/yarsu/public/img").resolve(safeFilename)

                Files.createDirectories(avatarPath.parent)
                Files.copy(file.content, avatarPath, StandardCopyOption.REPLACE_EXISTING)

                avatarFileName = safeFilename

                println("Файл сохранён как: $safeFilename")
                println("Путь до файла: ${avatarPath.toAbsolutePath()}")
            } ?: println("Файл не был загружен.")
        }

        val updatedProfile = Profile(
            id = profileId,
            name = name,
            description = description,
            instruments = existingProfile.instruments,
            styles = existingProfile.styles,
            avatarFileName = avatarFileName
        )

        profiles.updateProfile(profileId, updatedProfile)
        return Response(FOUND).header("Location", "/profile/${profileId}")
    }
}
