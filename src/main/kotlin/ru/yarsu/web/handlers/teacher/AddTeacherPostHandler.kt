package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import ru.yarsu.db.TeachersData
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.models.teacher.AddTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class AddTeacherPostHandler(
    private val htmlView: ContextAwareViewRender,
    private val teachers: TeachersData
) : HttpHandler {

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
        val form = formLens(request)
        val errors = form.errors.map { it.meta.name }.toList()

        val name = lensOrDefault(nameLens, form) { "" }
        val description = lensOrDefault(descriptionLens, form) { "" }
        val address = lensOrDefault(addressLens, form) { "" }
        val experienceYears = lensOrDefault(experienceYearsLens, form) { "1" }.toInt()
        val education = lensOrDefault(educationLens, form) { "" }
        val minStudentAge = lensOrDefault(minStudentAgeLens, form) { "1" }.toInt()

        val newId = teachers.getNextId()
        var avatarFileName: List<String> = emptyList()

        form.use {
            val file = imageLens(it)
            if (file != null && file.content.available() > 0) {
                val originalName = file.filename ?: "teacher_$newId.png"
                val extension = originalName.substringAfterLast('.', "png")
                val safeFilename = "teacher_$newId.$extension".replace(Regex("[^a-zA-Z0-9._-]"), "_")
                val avatarPath = Paths.get("src/main/resources/ru/yarsu/public/img").resolve(safeFilename)

                Files.createDirectories(avatarPath.parent)
                Files.copy(file.content, avatarPath, StandardCopyOption.REPLACE_EXISTING)

                println("Файл сохранён как: $safeFilename")
                avatarFileName = listOf(safeFilename)
            } else {
                println("Файл не был загружен или пуст.")
            }
        }

        val teacher = Teacher(
            id = newId,
            fullName = name,
            shortDescription = description,
            avatarFileName = avatarFileName,
            roles = PersonRole(listOf(1831874252), listOf(777990904)), // Заглушка
            address = Location(address),
            instruments = emptyList(),
            experienceInfo = ExperienceInfo(
                experienceYears = experienceYears,
                education = education,
                styles = emptyList(),
                minStudentAge = minStudentAge
            ),
            schedule = Schedule(emptyMap()),
        )

        // Если есть ошибки — показать форму с ошибками
        if (errors.isNotEmpty()) {
            val viewModel = AddTeacherVM(teacher, form)
            return Response(OK).with(htmlView(request) of viewModel)
        }

        teachers.addTeacher(teacher)

        return Response(FOUND).header("Location", "/teachers")
    }
}