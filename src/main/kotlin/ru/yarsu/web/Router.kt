package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.config.AppConfig
import ru.yarsu.db.SpotData
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.SessionStorage
import ru.yarsu.web.domain.article.TokenStorage
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.models.email.EmailService
import ru.yarsu.web.domain.models.telegram.JsonLogger
import ru.yarsu.web.filters.*
import ru.yarsu.web.handlers.admin.*
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
    users: UserData,
    spots: SpotData,
    sessionStorage: SessionStorage,
    userLens: RequestContextLens<UserModel?>,
): RoutingHttpHandler {
    val tokenStorage = TokenStorage()
    val emailService = EmailService(config)
    val jsonLogger = JsonLogger(config.telegramConfig.userDataFile)

    val filters = RouterFilters(userLens, spots)

    val routeGroups =
        RouterGroups(
            renderer,
            htmlView,
            config,
            users,
            spots,
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
        // Маршруты для директоров/админов
        filters.directorAdminFilter.then(routeGroups.directorOrAdminRoutes),
        // Маршруты только для админов
        filters.adminFilter.then(routeGroups.adminRoutes),
    )
}

// Класс для организации фильтров
private class RouterFilters(
    userLens: RequestContextLens<UserModel?>,
    spots: SpotData,
) {
    val authFilter = authenticatedOnlyFilter(userLens)
    val adminFilter = onlyAdminFilter(userLens)
    val directorAdminFilter = directorOrAdminFilter(userLens)
    val notTeacherFilter = notTeacherFilter(userLens)
    val teacherFilter = teacherOrAdminFilter(userLens)
    val spotOwnerFilter = spotOwnerOrAdminFilter(userLens, spots)
    val notOwnerFilter = notOwnerOrPendingOwnerFilter(userLens)
    val ownerFilter = ownerOrDirectorOrAdminFilter(userLens)
}

// Класс для организации групп маршрутов
private class RouterGroups(
    renderer: ContextAwareTemplateRenderer,
    htmlView: ContextAwareViewRender,
    config: AppConfig,
    private val users: UserData,
    spots: SpotData,
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
            "/auth/signin" bind Method.POST to AuthPostHandler(users, sessionStorage),
            "/auth/telegram" bind Method.POST to
                TelegramAuthPostHandler(
                    jsonLogger = jsonLogger,
                    botToken = config.telegramConfig.botToken,
                    users = users,
                    authSalt = config.webConfig.authSalt,
                ),
            "/auth/google" bind Method.POST to GmailAuthPostHandler(users, config),
            "/auth/forgot-password" bind Method.GET to ResetForgotPasswordGetHandler(renderer),
            "/auth/forgot-password" bind Method.POST to ResetForgotPasswordPostHandler(users, tokenStorage, emailService),
            "/auth/reset-password" bind Method.GET to ResetPasswordGetHandler(tokenStorage, renderer),
            "/auth/reset-password" bind Method.POST to ResetPasswordPostHandler(users, tokenStorage),
            "/auth/register" bind Method.POST to EmailRegisterPostHandler(users, tokenStorage, emailService),
            "/auth/confirm" bind Method.GET to EmailConfirmHandler(tokenStorage, users, renderer),
            // Public data
            "/teachers" bind Method.GET to TeachersGetHandler(htmlView, users),
            "/teacher/{id}" bind Method.GET to TeacherGetHandler(htmlView, users),
            "/spots" bind Method.GET to SpotsListHandler(htmlView, spots),
            "/spot/{id}" bind Method.GET to SpotGetHandler(htmlView, spots),
            "/profile/{id}" bind Method.GET to ProfileGetHandler(htmlView, users),
        )

    val authenticatedRoutes =
        routes(
            "/auth/signout" bind Method.GET to SignOutHandler(),
            "/auth/attach-telegram" bind Method.GET to
                AttachTelegramGetHandler(
                    users,
                    jsonLogger,
                    config.telegramConfig.botToken,
                ),
            "/auth/attach-telegram" bind Method.POST to
                AttachTelegramHandler(
                    users,
                    jsonLogger,
                    config.telegramConfig.botToken,
                ),
            "/teacher/{id}" bind Method.POST to TeacherPostHandler(users),
            "/spot/{id}" bind Method.POST to SpotPostHandler(spots, users),
        )

    val teacherRoutes =
        routes(
            "/edit/profile/edit-{id}" bind Method.GET to EditProfileGetHandler(htmlView, users),
            "/edit/profile/edit-{id}" bind Method.POST to EditProfilePostHandler(htmlView, users),
            "/edit/teacher/edit-{id}" bind Method.GET to EditTeacherGetHandler(htmlView, users),
            "/edit/teacher/edit-{id}" bind Method.POST to EditTeacherPostHandler(htmlView, users),
            "/edit/teacher/delete-{id}" bind Method.GET to DeleteTeacherGetHandler(htmlView, users),
            "/edit/teacher/delete-{id}" bind Method.POST to DeleteTeacherPostHandler(users),
        )

    val spotOwnerOrAdminRoutes =
        routes(
            "/edit/spot/edit-{id}" bind Method.GET to EditSpotGetHandler(htmlView, spots),
            "/edit/spot/edit-{id}" bind Method.POST to EditSpotPostHandler(htmlView, spots),
            "/edit/spot/delete-{id}" bind Method.GET to DeleteSpotGetHandler(htmlView, spots),
            "/edit/spot/delete-{id}" bind Method.POST to DeleteSpotPostHandler(spots),
        )

    val notOwnerRoutes =
        routes(
            "/upgrade/owner/{id}" bind Method.GET to UpgradeUserToOwnerGetHandler(htmlView, users),
            "/upgrade/owner/{id}" bind Method.POST to UpgradeUserToOwnerPostHandler(htmlView, users),
        )

    val ownerRoutes =
        routes(
            "/edit/spot/add" bind Method.GET to AddSpotGetHandler(htmlView),
            "/edit/spot/add" bind Method.POST to AddSpotPostHandler(htmlView, spots),
        )

    val adminRoutes =
        routes(
            "/admin/user/delete/{id}" bind Method.POST to DeleteUserHandler(users, spots),
        )

    val directorOrAdminRoutes =
        routes(
            "/admin/users" bind Method.GET to UserListHandler(htmlView, users),
            "/admin/user/{id}" bind Method.GET to UserGetHandler(htmlView, users),
            "/admin/user/roles/teacher/add/{id}" bind Method.POST to AddTeacherRoleHandler(users),
            "/admin/user/roles/teacher/remove/{id}" bind Method.POST to RemoveTeacherRoleHandler(users),
            "/admin/user/roles/owner/add/{id}" bind Method.POST to AddOwnerRoleHandler(users),
            "/admin/user/roles/owner/remove/{id}" bind Method.POST to RemoveOwnerRoleHandler(users),
            "/upgrade/teachers" bind Method.GET to UpgradeTeacherListGetHandler(htmlView, users),
            "/upgrade/teacher/profiles/{id}" bind Method.GET to UpgradeTeacherProfileGetHandler(htmlView, users),
            "/upgrade/teacher/accept/{id}" bind Method.POST to AcceptTeacherPostHandler(users),
            "/upgrade/teacher/reject/{id}" bind Method.POST to RejectTeacherPostHandler(users),
            "/upgrade/owners" bind Method.GET to UpgradeOwnerListGetHandler(htmlView, users),
            "/upgrade/owner/profiles/{id}" bind Method.GET to UpgradeOwnerProfileGetHandler(htmlView, users),
            "/upgrade/owner/accept/{id}" bind Method.POST to AcceptOwnerPostHandler(users),
            "/upgrade/owner/reject/{id}" bind Method.POST to RejectOwnerPostHandler(users),
        )

    val notTeacherOrPendingTeacherRoutes =
        routes(
            "/upgrade/teacher/{id}" bind Method.GET to UpgradeUserToTeacherGetHandler(htmlView, users),
            "/upgrade/teacher/{id}" bind Method.POST to UpgradeUserToTeacherPostHandler(htmlView, users),
        )
}
