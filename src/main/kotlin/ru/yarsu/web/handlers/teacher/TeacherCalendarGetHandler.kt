package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import ru.yarsu.web.models.teacher.TeacherCalendar2VM
import ru.yarsu.web.templates.ContextAwareViewRender

class TeacherCalendarGetHandler(private val htmlView: ContextAwareViewRender,): HttpHandler {

    override fun invoke(request: Request): Response {


        val viewModel = TeacherCalendar2VM()
        return Response(Status.OK).with(htmlView(request) of viewModel)
    }



}