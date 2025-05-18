package ru.yarsu.web

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.config.AppConfig
import ru.yarsu.db.ProfilesData
import ru.yarsu.db.StudiosData
import ru.yarsu.db.TeachersData
import ru.yarsu.web.domain.models.telegram.JsonLogger
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.handlers.home.HomePageHandler
import ru.yarsu.web.handlers.profile.EditProfileGetHandler
import ru.yarsu.web.handlers.profile.ProfileGetHandler
import ru.yarsu.web.handlers.studio.*
import ru.yarsu.web.handlers.teacher.*
import ru.yarsu.web.handlers.telegramAuth.TelegramAuthGetHandler
import ru.yarsu.web.handlers.telegramAuth.TelegramAuthPostHandler
import ru.yarsu.web.templates.ContextAwareViewRender

class TestErrorHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        throw RuntimeException("Искусственная ошибка 500 для теста.")
    }
}

fun router(
    htmlView: ContextAwareViewRender,
    config: AppConfig,
    teachers: TeachersData,
    studios: StudiosData,
    profiles: ProfilesData
): RoutingHttpHandler {
    return routes(
        "/" bind Method.GET to HomePageHandler(htmlView),
        "/test-error" bind Method.GET to TestErrorHandler(),
        "/ping" bind Method.GET to PingHandler(),
        "/auth/telegram" bind Method.GET to TelegramAuthGetHandler(htmlView),
        "/auth/telegram" bind Method.POST to TelegramAuthPostHandler(
            jsonLogger = JsonLogger(config.telegramConfig.userDataFile),
            botToken = config.telegramConfig.botToken
        ),
        "/profile/{id}" bind Method.GET to ProfileGetHandler(htmlView, profiles),
        "/edit/profile/edit-{id}" bind Method.GET to EditProfileGetHandler(htmlView, profiles),
//        "/edit/profile/edit-{id}" bind Method.POST to EditProfilePostHandler(htmlView, profiles),

        "/studio/{id}" bind Method.GET to StudioGetHandler(htmlView, studios),
//        "/studio" bind Method.POST to StudioPostHandler(config.telegramConfig.botToken, 1831874252.toString()),
        "/studios" bind Method.GET to StudiosGetHandler(htmlView, studios),
//        "/teacher" bind Method.POST to TeacherPostHandler(config.telegramConfig.botToken, 1831874252.toString()),
        "/teachers" bind Method.GET to TeachersGetHandler(htmlView, teachers),
        "/teacher/{id}" bind Method.GET to TeacherGetHandler(htmlView, teachers),


        "/edit/teacher/add" bind Method.GET to AddTeacherGetHandler(htmlView, teachers),
//        "/edit/teacher/add" bind Method.GET to AddTeacherGetHandler(htmlView),

        "/edit/teacher/edit-{id}" bind Method.GET to EditTeacherGetHandler(htmlView, teachers),
//        "/edit/teacher/edit-{id}" bind Method.POST to EditTeacherPostHandler(htmlView),

        "/edit/teacher/delete-{id}" bind Method.GET to DeleteTeacherGetHandler(htmlView, teachers),
        "/edit/teacher/delete-{id}" bind Method.POST to DeleteTeacherPostHandler(htmlView, teachers),


        "/edit/studio/edit-{id}" bind Method.GET to EditStudioGetHandler(htmlView, studios),
//        "/edit/studio/edit-{id}" bind Method.POST to EditStudioPostHandler(htmlView),

        "/edit/studio/add" bind Method.GET to AddStudioGetHandler(htmlView, studios),
//        "/edit/studio/add" bind Method.POST to AddStudioPostHandler(htmlView),

        "/edit/studio/delete-{id}" bind Method.GET to DeleteStudioGetHandler(htmlView, studios),
        "/edit/studio/delete-{id}" bind Method.POST to DeleteStudioPostHandler(htmlView, studios),
    )
}
