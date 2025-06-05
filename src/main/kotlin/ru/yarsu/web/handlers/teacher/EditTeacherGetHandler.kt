package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.models.teacher.EditTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditTeacherGetHandler(private val htmlView: ContextAwareViewRender, private val teachers: UserData) :
    HttpHandler {

    private val pathLens = Path.long().of("id")

    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")

    private val formLens = Body.multipartForm(
        Validator.Feedback,
        nameLens,
        descriptionLens,
    ).toLens()

    override fun invoke(request: Request): Response {
        val teacherId = request.path("id")?.toIntOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        val teacher = teachers.getTeacherById(teacherId)
            ?: return Response(Status.NOT_FOUND).body("Преподаватель не найден")

        val allAbility = AbilityEnums.entries

        val filledForm = MultipartForm()
            .with(nameLens of teacher.name)
            .with(descriptionLens of teacher.description)

        val viewModel = EditTeacherVM(
            teacher,
            allAbility,
            filledForm
        )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}