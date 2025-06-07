package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.lens.*
import org.http4k.routing.path
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.models.profile.EditProfileVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditProfileGetHandler(
    private val htmlView: ContextAwareViewRender,
    private val users: UserData,
) : HttpHandler {
    private val pathLens = Path.long().of("id")
    private val nameLens = MultipartFormField.string().required("name")
    private val descriptionLens = MultipartFormField.string().required("description")
    private val abilityLens = MultipartFormField.multi.required("ability")

    private val formLens =
        Body
            .multipartForm(
                Validator.Feedback,
                nameLens,
                descriptionLens,
                abilityLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val userId =
            request.path("id")?.toIntOrNull()
                ?: return Response(BAD_REQUEST).body("Некорректный ID профиля")

        val user =
            users.getById(userId)
                ?: return Response(NOT_FOUND).body("Профиль не найден")

        val allAbility = AbilityEnums.entries

        val filledForm =
            MultipartForm()
                .with(nameLens of user.name)
                .with(descriptionLens of user.description)

        val viewModel =
            EditProfileVM(
                user,
                allAbility,
                filledForm,
            )

        return Response(Status.OK).with(htmlView(request) of viewModel)
    }
}
