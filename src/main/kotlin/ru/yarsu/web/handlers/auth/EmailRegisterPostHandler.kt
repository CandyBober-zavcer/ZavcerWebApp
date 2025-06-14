package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.lens.*
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.models.email.EmailService

class EmailRegisterPostHandler(
    private val databaseController: DatabaseController,
    private val tokenStorage: TokenStorage,
    private val emailService: EmailService,
) : HttpHandler {
    private val loginLens = FormField.nonEmptyString().required("email")
    private val passwordLens = FormField.nonEmptyString().required("password")
    private val generatedAtLens = FormField.long().required("formGeneratedAt")
    private val honeypotLens = FormField.string().defaulted("website", "")

    private val formLens =
        Body
            .webForm(
                Validator.Strict,
                loginLens,
                passwordLens,
                generatedAtLens,
                honeypotLens,
            ).toLens()

    override fun invoke(request: Request): Response {
        val form =
            try {
                formLens(request)
            } catch (e: LensFailure) {
                return Response(Status.BAD_REQUEST).body("Неверные данные формы")
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

        val login = loginLens(form)
        val password = passwordLens(form)

        if (databaseController.getUserByLogin(login) != null) {
            return Response(Status.CONFLICT).body("Пользователь с таким email уже существует")
        }

        val newUser =
            databaseController.insertUser(
                User(
                    login = login,
                    password = password,
                    isConfirmed = false,
                )
            )

        val token = tokenStorage.generateConfirmationToken(newUser)
        val confirmationLink = "https://zavcer.ru.tuna.am/auth/confirm?token=$token"
        val message =
            """
            <html>
            <body>
                <p>Здравствуйте!</p>
                <p>Для подтверждения регистрации перейдите по ссылке:</p>
                <p><a href="$confirmationLink">$confirmationLink</a></p>
                <p>Срок действия ссылки: 15 минут.</p>
            </body>
            </html>
            """.trimIndent()

        emailService.send(login, "Подтверждение регистрации", message)

        return Response(Status.OK).body("Письмо с подтверждением отправлено. Проверьте почту.")
    }
}
