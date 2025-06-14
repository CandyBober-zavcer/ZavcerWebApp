package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.BiDiLens
import ru.yarsu.web.domain.classes.User

fun authenticatedOnlyFilter(userLens: BiDiLens<Request, User?>): Filter =
    Filter { next ->
        { request ->
            userLens(request)?.let { next(request) }
                ?: Response(Status.UNAUTHORIZED).body("Please sign in to continue")
        }
    }