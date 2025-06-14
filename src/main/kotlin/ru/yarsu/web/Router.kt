package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.config.AppConfig
import ru.yarsu.db.DatabaseController
import ru.yarsu.web.domain.article.SessionStorage
import ru.yarsu.web.domain.article.TokenStorage
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.models.email.EmailService
import ru.yarsu.web.domain.models.telegram.JsonLogger
import ru.yarsu.web.filters.*
import ru.yarsu.web.handlers.auth.*
import ru.yarsu.web.handlers.home.HomePageHandler
import ru.yarsu.web.handlers.profile.*
import ru.yarsu.web.handlers.spot.*
import ru.yarsu.web.handlers.teacher.*
import ru.yarsu.web.handlers.upgrade.*
import ru.yarsu.web.templates.ContextAwareTemplateRenderer
import ru.yarsu.web.templates.ContextAwareViewRender

fun router(
    renderer: ContextAwareTemplateRenderer,
    htmlView: ContextAwareViewRender,
    config: AppConfig,
    databaseController: DatabaseController,
    sessionStorage: SessionStorage,
    userLens: RequestContextLens<User?>,
): RoutingHttpHandler {
    val tokenStorage = TokenStorage()
    val emailService = EmailService(config)
    val jsonLogger = JsonLogger(config.telegramConfig.userDataFile)

    val filters = RouterFilters(userLens, databaseController)

    val routeGroups =
        RouterGroups(
            renderer,
            htmlView,
            config,
            databaseController,
            sessionStorage,
            tokenStorage,
            emailService,
            jsonLogger,
        )

    return routes(
        // Публичные маршруты
        routeGroups.publicRoutes,
        // Маршруты, требующие авторизации
        filters.authFilter.then(routeGroups.authenticatedRoutes),
        // Маршруты для учителей
        filters.teacherFilter.then(routeGroups.teacherRoutes),
        // Маршруты для владельцев спотов
        filters.spotOwnerFilter.then(routeGroups.spotOwnerOrAdminRoutes),
        // Маршруты для не-владельцев
        filters.notOwnerFilter.then(routeGroups.notOwnerRoutes),
        // Маршруты для владельцев/директоров/админов
        filters.ownerFilter.then(routeGroups.ownerRoutes),
        // Маршруты для не-учителей
        filters.notTeacherFilter.then(routeGroups.notTeacherOrPendingTeacherRoutes),
    )
}

// Класс для организации фильтров
private class RouterFilters(
    userLens: RequestContextLens<User?>,
    databaseController: DatabaseController,
) {
    val authFilter = authenticatedOnlyFilter(userLens)
    val notTeacherFilter = notTeacherFilter(userLens)
    val teacherFilter = teacherOrAdminFilter(userLens)
    val spotOwnerFilter = spotOwnerOrAdminFilter(userLens, databaseController)
    val notOwnerFilter = notOwnerOrPendingOwnerFilter(userLens)
    val ownerFilter = ownerOrDirectorOrAdminFilter(userLens)
}

// Класс для организации групп маршрутов
private class RouterGroups(
    renderer: ContextAwareTemplateRenderer,
    htmlView: ContextAwareViewRender,
    config: AppConfig,
    private val databaseController: DatabaseController,
    sessionStorage: SessionStorage,
    tokenStorage: TokenStorage,
    emailService: EmailService,
    private val jsonLogger: JsonLogger,
) {
    val publicRoutes =
        routes(
            // Home
            "/" bind Method.GET to HomePageHandler(htmlView),
            // Authentication
            "/auth/signup" bind Method.GET to SignupGetHandler(htmlView),
            "/auth/signin" bind Method.GET to AuthGetHandler(htmlView),
            "/auth/signin" bind Method.POST to AuthPostHandler(databaseController, sessionStorage),
            "/auth/telegram" bind Method.POST to
                    TelegramAuthPostHandler(
                        jsonLogger = jsonLogger,
                        botToken = config.telegramConfig.botToken,
                        databaseController = databaseController,
                        authSalt = config.webConfig.authSalt,
                    ),
            "/auth/google" bind Method.POST to GmailAuthPostHandler(databaseController, config),
            "/auth/forgot-password" bind Method.GET to ResetForgotPasswordGetHandler(renderer),
            "/auth/forgot-password" bind Method.POST to ResetForgotPasswordPostHandler(databaseController, tokenStorage, emailService),
            "/auth/reset-password" bind Method.GET to ResetPasswordGetHandler(tokenStorage, renderer),
            "/auth/reset-password" bind Method.POST to ResetPasswordPostHandler(databaseController, tokenStorage),
            "/auth/register" bind Method.POST to EmailRegisterPostHandler(databaseController, tokenStorage, emailService),
            "/auth/confirm" bind Method.GET to EmailConfirmHandler(tokenStorage, databaseController, renderer),
            // Public data
            "/teachers" bind Method.GET to TeachersGetHandler(htmlView, databaseController),
            "/teacher/{id}" bind Method.GET to TeacherGetHandler(htmlView, databaseController),
            "/spots" bind Method.GET to SpotsListHandler(htmlView, databaseController),
            "/spot/{id}" bind Method.GET to SpotGetHandler(htmlView, databaseController),
            "/profile/{id}" bind Method.GET to ProfileGetHandler(htmlView, databaseController),

            "schedule/teacher/{id}" bind Method.GET to TeacherScheduleGetHandler(htmlView, databaseController),
            "schedule/owner/viewing/{id}" bind Method.GET to SpotScheduleViewingHandler(htmlView, databaseController),
            "schedule/owner/editing/{id}" bind Method.GET to SpotScheduleEditingGetHandler(htmlView, databaseController),
        )

    val authenticatedRoutes =
        routes(
            "/auth/signout" bind Method.GET to SignOutHandler(),
            "/auth/attach-telegram" bind Method.GET to
                    AttachTelegramGetHandler(
                        databaseController,
                        jsonLogger,
                        config.telegramConfig.botToken,
                    ),
            "/auth/attach-telegram" bind Method.POST to
                    AttachTelegramHandler(
                        databaseController,
                        jsonLogger,
                        config.telegramConfig.botToken,
                    ),
            "/teacher/{id}" bind Method.POST to TeacherPostHandler(databaseController),
            "/spot/{id}" bind Method.POST to SpotPostHandler(databaseController),
        )

    val teacherRoutes =
        routes(
            "/edit/profile/edit-{id}" bind Method.GET to EditProfileGetHandler(htmlView, databaseController),
            "/edit/profile/edit-{id}" bind Method.POST to EditProfilePostHandler(htmlView, databaseController),
            "/edit/teacher/edit-{id}" bind Method.GET to EditTeacherGetHandler(htmlView, databaseController),
            "/edit/teacher/edit-{id}" bind Method.POST to EditTeacherPostHandler(htmlView, databaseController),
            "/edit/teacher/delete-{id}" bind Method.GET to DeleteTeacherGetHandler(htmlView, databaseController),
            "/edit/teacher/delete-{id}" bind Method.POST to DeleteTeacherPostHandler(databaseController),
        )

    val spotOwnerOrAdminRoutes =
        routes(
            "/edit/spot/edit-{id}" bind Method.GET to EditSpotGetHandler(htmlView, databaseController),
            "/edit/spot/edit-{id}" bind Method.POST to EditSpotPostHandler(htmlView, databaseController),
            "/edit/spot/delete-{id}" bind Method.GET to DeleteSpotGetHandler(htmlView, databaseController),
            "/edit/spot/delete-{id}" bind Method.POST to DeleteSpotPostHandler(databaseController),
        )

    val notOwnerRoutes =
        routes(
            "/upgrade/owner/{id}" bind Method.GET to UpgradeUserToOwnerGetHandler(htmlView, databaseController),
            "/upgrade/owner/{id}" bind Method.POST to UpgradeUserToOwnerPostHandler(htmlView, databaseController),
        )

    val ownerRoutes =
        routes(
            "/edit/spot/add" bind Method.GET to AddSpotGetHandler(htmlView),
            "/edit/spot/add" bind Method.POST to AddSpotPostHandler(htmlView, databaseController),
        )

    val notTeacherOrPendingTeacherRoutes =
        routes(
            "/upgrade/teacher/{id}" bind Method.GET to UpgradeUserToTeacherGetHandler(htmlView, databaseController),
            "/upgrade/teacher/{id}" bind Method.POST to UpgradeUserToTeacherPostHandler(htmlView, databaseController),
        )
}