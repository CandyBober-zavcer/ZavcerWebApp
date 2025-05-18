package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.TeachersData
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.studio.EditStudioVM
import ru.yarsu.web.models.teacher.EditTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class EditTeacherPostHandler(
    private val htmlView: ContextAwareViewRender,
    private val teachers: TeachersData
) : HttpHandler {

    private val pathLens = Path.long().of("id")

    private val nameLens = MultipartFormField.string().required("fullName")
    private val descriptionLens = MultipartFormField.string().required("shortDescription")
    private val imageLens = MultipartFormFile.optional("photo")
    private val experienceYearsLens = MultipartFormField.string().required("experienceYears")
    private val educationLens = MultipartFormField.string().required("education")
    private val minStudentAgeLens = MultipartFormField.string().required("minStudentAge")
    private val addressLens = MultipartFormField.string().required("address")

    private val formLens = Body.multipartForm(
        Validator.Feedback,
        nameLens,
        descriptionLens,
        imageLens,
        experienceYearsLens,
        educationLens,
        minStudentAgeLens,
        addressLens,
    ).toLens()

    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)
        val teacherId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Неверный ID преподавателя")

        val existingTeacher = teachers.getTeacherById(teacherId)
            ?: return Response(NOT_FOUND).body("Преподаватель не найден")

        val form = formLens(request)
        val errors = form.errors.map { it.meta.name }
        val allInstruments = Instrument.entries
        val allStyles = MusicStyle.entries

        if (errors.isNotEmpty()) {
            val viewModel = EditTeacherVM(
                teacher = existingTeacher,
                user = user?.id?.toString() ?: "null",
                allStyles = allStyles,
                allInstruments = allInstruments,
                form = form,
            )
            return Response(OK).with(htmlView(request) of viewModel)
        }

        val name = lensOrDefault(nameLens, form) { existingTeacher.fullName }
        val description = lensOrDefault(descriptionLens, form) { existingTeacher.shortDescription }
        val address = lensOrDefault(addressLens, form) { existingTeacher.address.address }
        val experienceYears = lensOrDefault(experienceYearsLens, form) { existingTeacher.experienceInfo.experienceYears.toString() }.toInt()
        val education = lensOrDefault(educationLens, form) { existingTeacher.experienceInfo.education }
        val minStudentAge = lensOrDefault(minStudentAgeLens, form) { existingTeacher.experienceInfo.minStudentAge.toString() }.toInt()

        println("Имя: $name")
        println("Описание: $description")

        var avatarFileName = existingTeacher.avatarFileName

        form.use {
            val file = imageLens(it)
            if (file != null && file.content.available() > 0) {
                val originalName = file.filename ?: "teacher_${teacherId}.png"
                val extension = originalName.substringAfterLast('.', "png")
                val filename = "teacher_${teacherId}.$extension"
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


        val updateTeacher = Teacher(
            id = teacherId,
            fullName = name,
            shortDescription = description,
            avatarFileName = avatarFileName,
            roles = existingTeacher.roles,
            address = Location(address),
            experienceInfo = ExperienceInfo(
                experienceYears = experienceYears,
                education = education,
                styles = emptyList(),
                minStudentAge = minStudentAge,
            ),
            instruments = emptyList(),
            schedule = existingTeacher.schedule,
        )

        teachers.updateTeacher(teacherId, updateTeacher)
        return Response(FOUND).header("Location", "/teacher/${teacherId}")
    }
}