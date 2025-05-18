package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.TeachersData
import ru.yarsu.web.domain.article.Instrument
import ru.yarsu.web.domain.article.MusicStyle
import ru.yarsu.web.domain.models.telegram.AuthUtils
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.models.studio.EditStudioVM
import ru.yarsu.web.models.teacher.EditTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditTeacherGetHandler(private val htmlView: ContextAwareViewRender, private val teachers: TeachersData) :
    HttpHandler {

    private val pathLens = Path.long().of("id")

    private val nameLens = MultipartFormField.string().required("fullName")
    private val descriptionLens = MultipartFormField.string().required("shortDescription")

    private val formLens = Body.multipartForm(
        Validator.Feedback,
        nameLens,
        descriptionLens,
    ).toLens()

    override fun invoke(request: Request): Response {
        val user = AuthUtils.getUserFromCookie(request)
        val teacherId = request.path("id")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        val teacher = teachers.getTeacherById(teacherId)
            ?: return Response(Status.NOT_FOUND).body("Преподаватель не найден")
        val allStyles = MusicStyle.entries
        val allInstruments = Instrument.entries

        val filledForm = MultipartForm()
            .with(nameLens of teacher.fullName)
            .with(descriptionLens of teacher.shortDescription)

        val viewModel = EditTeacherVM(
            teacher,
            user = user?.id?.toString() ?: "null",
            allStyles,
            allInstruments,
            filledForm
        )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }

}