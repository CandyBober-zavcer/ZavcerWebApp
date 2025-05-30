package ru.yarsu.web

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.config.AppConfig
import ru.yarsu.db.StudiosData
import ru.yarsu.db.TeachersData
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.SessionStorage
import ru.yarsu.web.domain.article.TokenStorage
import ru.yarsu.web.domain.models.email.EmailService
import ru.yarsu.web.domain.models.telegram.JsonLogger
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.handlers.auth.*
import ru.yarsu.web.handlers.home.HomePageHandler
import ru.yarsu.web.handlers.profile.EditProfileGetHandler
import ru.yarsu.web.handlers.profile.EditProfilePostHandler
import ru.yarsu.web.handlers.profile.ProfileGetHandler
import ru.yarsu.web.handlers.studio.*
import ru.yarsu.web.handlers.teacher.*
import ru.yarsu.web.handlers.upgrade.UpgradeUserToTeacherGetHandler
import ru.yarsu.web.handlers.upgrade.UpgradeUserToTeacherPostHandler
import ru.yarsu.web.templates.ContextAwareTemplateRenderer
import ru.yarsu.web.templates.ContextAwareViewRender

class TestErrorHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        throw RuntimeException("Искусственная ошибка 500 для теста.")
    }
}

fun router(
    renderer: ContextAwareTemplateRenderer,
    htmlView: ContextAwareViewRender,
    config: AppConfig,
    teachers: TeachersData,
    studios: StudiosData,
    users: UserData,
    sessionStorage: SessionStorage
): RoutingHttpHandler {

    val tokenStorage = TokenStorage()
    val emailService = EmailService(config)

    return routes(
        "/" bind Method.GET to HomePageHandler(htmlView),
        "/test-error" bind Method.GET to TestErrorHandler(),
        "/ping" bind Method.GET to PingHandler(users),
        "/auth/signup" bind Method.GET to SignupGetHandler(htmlView),
        "/auth/signin" bind Method.GET to AuthGetHandler(htmlView),
        "/auth/signin" bind Method.POST to AuthPostHandler(users, sessionStorage),
        "/auth/telegram" bind Method.POST to TelegramAuthPostHandler(
            jsonLogger = JsonLogger(config.telegramConfig.userDataFile),
            botToken = config.telegramConfig.botToken,
            users = users,
            authSalt = config.webConfig.authSalt
        ),
        "/auth/google" bind Method.POST to GmailAuthPostHandler(users, config),
        "/auth/forgot-password" bind Method.GET to ResetForgotPasswordGetHandler(renderer),
        "/auth/forgot-password" bind Method.POST to ResetForgotPasswordPostHandler(users, tokenStorage, emailService),
        "/auth/reset-password" bind Method.GET to ResetPasswordGetHandler(tokenStorage, renderer),
        "/auth/reset-password" bind Method.POST to ResetPasswordPostHandler(users, tokenStorage),
        "/auth/signout" bind Method.GET to SignOutHandler(),
        "/auth/register" bind Method.POST to EmailRegisterPostHandler(users, tokenStorage, emailService),
        "/auth/confirm" bind Method.GET to EmailConfirmHandler(tokenStorage, users, renderer),
        "/profile/{id}" bind Method.GET to ProfileGetHandler(htmlView, users),
        "/edit/profile/edit-{id}" bind Method.GET to EditProfileGetHandler(htmlView, users),
        "/edit/profile/edit-{id}" bind Method.POST to EditProfilePostHandler(htmlView, users),

        "/studio/{id}" bind Method.GET to StudioGetHandler(htmlView, studios),
//        "/studio" bind Method.POST to StudioPostHandler(config.telegramConfig.botToken, 1831874252.toString()),
        "/studios" bind Method.GET to StudiosGetHandler(htmlView, studios),

        "/upgrade/teacher/{id}" bind Method.GET to UpgradeUserToTeacherGetHandler(htmlView, users),
        "/upgrade/teacher/{id}" bind Method.POST to UpgradeUserToTeacherPostHandler(htmlView, users),
//        "/teacher" bind Method.POST to TeacherPostHandler(config.telegramConfig.botToken, 1831874252.toString()),
        "/teachers" bind Method.GET to TeachersGetHandler(htmlView, users),
        "/teacher/{id}" bind Method.GET to TeacherGetHandler(htmlView, users),
//
//        "/edit/teacher/add" bind Method.GET to AddTeacherGetHandler(htmlView, teachers),
//        "/edit/teacher/add" bind Method.POST to AddTeacherPostHandler(htmlView, teachers),
//
        "/edit/teacher/edit-{id}" bind Method.GET to EditTeacherGetHandler(htmlView, users),
        "/edit/teacher/edit-{id}" bind Method.POST to EditTeacherPostHandler(htmlView, users),

        "/edit/teacher/delete-{id}" bind Method.GET to DeleteTeacherGetHandler(htmlView, users),
        "/edit/teacher/delete-{id}" bind Method.POST to DeleteTeacherPostHandler(users),

        "/edit/studio/edit-{id}" bind Method.GET to EditStudioGetHandler(htmlView, studios),
        "/edit/studio/edit-{id}" bind Method.POST to EditStudioPostHandler(htmlView, studios),

        "/edit/studio/add" bind Method.GET to AddStudioGetHandler(htmlView),
        "/edit/studio/add" bind Method.POST to AddStudioPostHandler(htmlView, studios),

        "/edit/studio/delete-{id}" bind Method.GET to DeleteStudioGetHandler(htmlView, studios),
        "/edit/studio/delete-{id}" bind Method.POST to DeleteStudioPostHandler(htmlView, studios),
    )
}
