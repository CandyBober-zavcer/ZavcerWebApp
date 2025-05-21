package ru.yarsu.web.handlers.auth

import org.http4k.core.*
import org.http4k.lens.*
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.*
import ru.yarsu.web.domain.models.email.EmailService
import ru.yarsu.web.domain.models.email.hashPassword
import java.util.*

class EmailRegisterPostHandler(
    private val userData: UserData,
    private val tokenStorage: TokenStorage,
    private val emailService: EmailService
) : HttpHandler {

    private val loginLens = FormField.nonEmptyString().required("email")
    private val passwordLens = FormField.nonEmptyString().required("password")
    private val formLens = Body.webForm(Validator.Strict, loginLens, passwordLens).toLens()

    override fun invoke(request: Request): Response {
        val form = try {
            formLens(request)
        } catch (e: LensFailure) {
            return Response(Status.BAD_REQUEST).body("Неверные данные формы")
        }

        val login = loginLens(form)
        val password = passwordLens(form)

        if (userData.existsByLogin(login)) {
            return Response(Status.CONFLICT).body("Пользователь с таким email уже существует")
        }

        val newUser = userData.add(
            UserModel(
                login = login,
                password = password,
                isConfirmed = false
            )
        )


        val token = tokenStorage.generateToken(newUser.id)
        val confirmationLink = "https://zavcer.ru.tuna.am/auth/confirm?token=$token"
        val message = """
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
