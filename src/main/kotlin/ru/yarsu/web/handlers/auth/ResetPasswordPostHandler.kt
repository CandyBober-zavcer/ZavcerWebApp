package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.lens.*
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.TokenStorage

class ResetPasswordPostHandler(
    private val userData: UserData,
    private val tokenStorage: TokenStorage,
) : HttpHandler {
    private val tokenLens = FormField.nonEmptyString().required("token")
    private val passwordLens = FormField.nonEmptyString().required("password")
    private val generatedAtLens = FormField.long().required("formGeneratedAt")
    private val honeypotLens = FormField.string().defaulted("website", "")

    private val formLens =
        Body
            .webForm(
                Validator.Strict,
                tokenLens,
                passwordLens,
                generatedAtLens,
                honeypotLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val form =
            try {
                formLens(request)
            } catch (e: LensFailure) {
                return Response(Status.BAD_REQUEST).body("Неверные данные формы.")
            }

        val honeypot = honeypotLens(form)
        if (honeypot.isNotBlank()) {
            return Response(Status.BAD_REQUEST).body("Похоже, вы бот.")
        }

        val generatedAt = generatedAtLens(form)
        val now = System.currentTimeMillis()
        val timeDiff = now - generatedAt

        if (timeDiff < 1000) {
            println("Форма отправлена слишком быстро: ${timeDiff}мс")
            return Response(Status.BAD_REQUEST).body("Слишком быстрая отправка формы")
        }

        val token = tokenLens(form)
        val newPassword = passwordLens(form)

        val email =
            tokenStorage.findEmailByResetToken(token)
                ?: return Response(Status.BAD_REQUEST).body("Ссылка устарела или недействительна.")

        val user =
            userData.getByEmail(email)
                ?: return Response(Status.NOT_FOUND).body("Пользователь не найден.")

        userData.updatePassword(user.id, newPassword)
        tokenStorage.remove(token)

        return Response(Status.FOUND).header("Location", "/auth/signin")
    }
}
