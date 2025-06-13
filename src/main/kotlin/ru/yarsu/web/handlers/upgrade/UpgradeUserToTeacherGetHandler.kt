package ru.yarsu.web.handlers.upgrade

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.models.upgrade.UpgradeUserToTeacherVM
import ru.yarsu.web.templates.ContextAwareViewRender

class UpgradeUserToTeacherGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val databaseController: DatabaseController,
) : HttpHandler {
    private val pathLens = Path.long().of("id")

    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")

    private val formLens =
        Body
            .multipartForm(
                Validator.Feedback,
                nameLens,
                descriptionLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID пользователя")

        val user =
            databaseController.getUserIfNotTeacher(userId)
                ?: return Response(Status.NOT_FOUND).body("Пользователь не найден или не может стать учителем")

        val allAbility = AbilityEnums.entries

        val filledForm =
            MultipartForm()
                .with(nameLens of user.name)
                .with(descriptionLens of user.description)

        val viewModel =
            UpgradeUserToTeacherVM(
                user,
                allAbility,
                filledForm,
            )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
