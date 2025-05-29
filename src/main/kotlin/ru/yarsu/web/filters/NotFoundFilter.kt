package ru.yarsu.web.filters

import org.http4k.core.*
import ru.yarsu.web.models.error.NotFoundVM
import ru.yarsu.web.templates.ContextAwareViewRender

class NotFoundFilter(
    private val htmlView: ContextAwareViewRender,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request ->
            val response = next(request)
            if (response.status == Status.NOT_FOUND) {
                Response(Status.NOT_FOUND).with(
                    htmlView(request) of
                        NotFoundVM(
                            code = "404",
                            message = "Страница '${request.uri.path}' не найдена",
                        ),
                )
            } else {
                response
            }
        }
}
