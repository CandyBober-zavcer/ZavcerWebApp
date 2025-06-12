package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.lens.*
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.TokenStorage
import ru.yarsu.web.domain.models.email.EmailService
import java.util.*

class ResetForgotPasswordPostHandler(
    private val userData: UserData,
    private val tokenStorage: TokenStorage,
    private val emailService: EmailService,
) : HttpHandler {
    private val emailLens = FormField.nonEmptyString().map({ it.trim() }, { it }).required("email")

    private val formLens =
        Body
            .webForm(
                Validator.Strict,
                emailLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val form =
            try {
                formLens(request)
            } catch (e: LensFailure) {
                return Response(Status.BAD_REQUEST).body("Неверный формат email.")
            }

        val email = emailLens(form)

        val user = userData.getByEmail(email)
        if (user != null && user.isConfirmed) {
            val token = UUID.randomUUID().toString()
            tokenStorage.saveResetPasswordToken(email, token)

            val resetLink = "http://zavcer.ru.tuna.am/auth/reset-password?token=$token"
            val message =
                """
                <html>
                <body>
                    <p>Здравствуйте!</p>
                    <p>Вы запросили сброс пароля для аккаунта <b>$email</b>.</p>
                    <p>Ссылка для сброса пароля:</p>
                    <p><a href="$resetLink">$resetLink</a></p>
                    <p>Срок действия ссылки: <b>1 час</b>.</p>
                    <p>Если вы не запрашивали сброс пароля — просто проигнорируйте это письмо.</p>
                </body>
                </html>
                """.trimIndent()

            emailService.send(email, "Сброс пароля", message)
        }

        return Response(Status.OK).body("Если email существует, мы отправили письмо со ссылкой на сброс пароля.")
    }
}
