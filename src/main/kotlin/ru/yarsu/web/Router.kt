package ru.yarsu.web

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.config.AppConfig
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.article.SessionStorage
import ru.yarsu.web.domain.article.TokenStorage
import ru.yarsu.web.domain.models.email.EmailService
import ru.yarsu.web.domain.models.telegram.JsonLogger
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.handlers.auth.*
import ru.yarsu.web.handlers.home.HomePageHandler
import ru.yarsu.web.handlers.profile.*
import ru.yarsu.web.handlers.profile.AttachTelegramGetHandler
import ru.yarsu.web.handlers.spot.AddSpotGetHandler
import ru.yarsu.web.handlers.spot.AddSpotPostHandler
import ru.yarsu.web.handlers.spot.DeleteSpotGetHandler
import ru.yarsu.web.handlers.spot.DeleteSpotPostHandler
import ru.yarsu.web.handlers.spot.EditSpotGetHandler
import ru.yarsu.web.handlers.spot.EditSpotPostHandler
import ru.yarsu.web.handlers.spot.SpotGetHandler
// import ru.yarsu.web.handlers.spot.SpotPostHandler
import ru.yarsu.web.handlers.spot.SpotsListHandler
import ru.yarsu.web.handlers.teacher.*
import ru.yarsu.web.handlers.upgrade.*
// import ru.yarsu.web.models.upgrade.UpgradeOwnerProfileVM
import ru.yarsu.web.templates.ContextAwareTemplateRenderer
import ru.yarsu.web.templates.ContextAwareViewRender

class TestErrorHandler : HttpHandler {
    override fun invoke(request: Request): Response = throw RuntimeException("Искусственная ошибка 500 для теста.")
}

fun router(
    renderer: ContextAwareTemplateRenderer,
    htmlView: ContextAwareViewRender,
    config: AppConfig,
    databaseController: DatabaseController,
    sessionStorage: SessionStorage,
): RoutingHttpHandler {
    val tokenStorage = TokenStorage()
    val emailService = EmailService(config)

    return routes(
        "/" bind Method.GET to HomePageHandler(htmlView),
        "/test-error" bind Method.GET to TestErrorHandler(),
        "/ping" bind Method.GET to PingHandler(databaseController),
        "/auth/signup" bind Method.GET to SignupGetHandler(htmlView),
        "/auth/signin" bind Method.GET to AuthGetHandler(htmlView),
        "/auth/signin" bind Method.POST to AuthPostHandler(databaseController, sessionStorage),
        "/auth/telegram" bind Method.POST to
            TelegramAuthPostHandler(
                jsonLogger = JsonLogger(config.telegramConfig.userDataFile),
                botToken = config.telegramConfig.botToken,
                databaseController = databaseController,
                authSalt = config.webConfig.authSalt,
            ),
        "/auth/google" bind Method.POST to GmailAuthPostHandler(databaseController, config),
        "/auth/forgot-password" bind Method.GET to ResetForgotPasswordGetHandler(renderer),
        "/auth/forgot-password" bind Method.POST to ResetForgotPasswordPostHandler(databaseController, tokenStorage, emailService),
        "/auth/reset-password" bind Method.GET to ResetPasswordGetHandler(tokenStorage, renderer),
        "/auth/reset-password" bind Method.POST to ResetPasswordPostHandler(databaseController, tokenStorage),
        "/auth/signout" bind Method.GET to SignOutHandler(),
        "/auth/register" bind Method.POST to EmailRegisterPostHandler(databaseController, tokenStorage, emailService),
        "/auth/confirm" bind Method.GET to EmailConfirmHandler(tokenStorage, databaseController, renderer),
        "/profile/{id}" bind Method.GET to ProfileGetHandler(htmlView, databaseController),
        "schedule/teacher/{id}" bind Method.GET to TeacherScheduleGetHandler(htmlView, databaseController),
        "schedule/owner/viewing/{id}" bind Method.GET to SpotScheduleViewingHandler(htmlView, databaseController),
        "schedule/owner/editing/{id}" bind Method.GET to SpotScheduleEditingGetHandler(htmlView, databaseController),
        "/edit/profile/edit-{id}" bind Method.GET to EditProfileGetHandler(htmlView, databaseController),
        "/edit/profile/edit-{id}" bind Method.POST to EditProfilePostHandler(htmlView, databaseController),
        "/auth/attach-telegram" bind Method.GET to
            AttachTelegramGetHandler(
                databaseController = databaseController,
                jsonLogger = JsonLogger(config.telegramConfig.userDataFile),
                botToken = config.telegramConfig.botToken,
            ),
        "/auth/attach-telegram" bind Method.POST to
            AttachTelegramHandler(
                jsonLogger = JsonLogger(config.telegramConfig.userDataFile),
                botToken = config.telegramConfig.botToken,
                databaseController = databaseController,
            ),
        "/upgrade/teacher/{id}" bind Method.GET to UpgradeUserToTeacherGetHandler(htmlView, databaseController),
        "/upgrade/teacher/{id}" bind Method.POST to UpgradeUserToTeacherPostHandler(htmlView, databaseController),
        "/upgrade/teachers" bind Method.GET to UpgradeListGetHandler(htmlView, databaseController),
//        "/upgrade/teacher/profiles/{id}" bind Method.GET to UpgradeProfileGetHandler(htmlView, databaseController),
        "/upgrade/teacher/accept/{id}" bind Method.POST to AcceptTeacherPostHandler(databaseController),
        "/upgrade/teacher/reject/{id}" bind Method.POST to RejectTeacherPostHandler(databaseController),
//        "/teacher" bind Method.POST to TeacherPostHandler(config.telegramConfig.botToken, 1831874252.toString()),
        "/teachers" bind Method.GET to TeachersGetHandler(htmlView, databaseController),
        "/teacher/{id}" bind Method.GET to TeacherGetHandler(htmlView, databaseController),
        "/edit/teacher/edit-{id}" bind Method.GET to EditTeacherGetHandler(htmlView, databaseController),
        "/edit/teacher/edit-{id}" bind Method.POST to EditTeacherPostHandler(htmlView, databaseController),
        "/edit/teacher/delete-{id}" bind Method.GET to DeleteTeacherGetHandler(htmlView, databaseController),
        "/edit/teacher/delete-{id}" bind Method.POST to DeleteTeacherPostHandler(databaseController),
        "/spots" bind Method.GET to SpotsListHandler(htmlView, databaseController),
        "/spot/{id}" bind Method.GET to SpotGetHandler(htmlView, databaseController),
        "/edit/spot/add" bind Method.GET to AddSpotGetHandler(htmlView),
        "/edit/spot/add" bind Method.POST to AddSpotPostHandler(htmlView, databaseController),
        "/edit/spot/edit-{id}" bind Method.GET to EditSpotGetHandler(htmlView, databaseController),
        "/edit/spot/edit-{id}" bind Method.POST to EditSpotPostHandler(htmlView, databaseController),
        "/edit/spot/delete-{id}" bind Method.GET to DeleteSpotGetHandler(htmlView, databaseController),
        "/edit/spot/delete-{id}" bind Method.POST to DeleteSpotPostHandler(databaseController),
    )
}
