package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.lens.MultipartForm
import org.http4k.lens.MultipartFormField
import ru.yarsu.db.TeachersData
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.models.teacher.AddTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class AddTeacherGetHandler(private val htmlView: ContextAwareViewRender, private val teachers: TeachersData): HttpHandler {

    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")

    override fun invoke(request: Request): Response {
        val entity = Teacher(
            id = 0L,
            fullName = "",
            shortDescription = "",
            avatarFileName = listOf("defaultTeacher.jpg"),
            roles = PersonRole(listOf(1831874252), listOf(777990904)),
            address = Location(""),
            experienceInfo = ExperienceInfo(
                experienceYears = 0,
                education = "",
                styles = emptyList(),
                minStudentAge = 0
            ),
            instruments = emptyList(),
            schedule = Schedule(emptyMap()),
        )


        val form = MultipartForm().with(
            nameLens of entity.fullName,
            descriptionLens of entity.shortDescription,
        )

        val viewModel = AddTeacherVM(entity, form)

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}