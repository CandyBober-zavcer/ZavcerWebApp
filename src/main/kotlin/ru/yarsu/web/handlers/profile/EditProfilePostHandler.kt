package ru.yarsu.web.handlers.profile

import org.http4k.core.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.lens.*
import ru.yarsu.db.ProfilesData
import ru.yarsu.web.domain.article.Profile
import ru.yarsu.web.funs.lensOrDefault
import ru.yarsu.web.funs.lensOrNull
import ru.yarsu.web.templates.ContextAwareViewRender
import java.util.*

//class EditProfilePostHandler(
//    private val htmlView: ContextAwareViewRender,
//    private val profiles: ProfilesData
//) : HttpHandler {
//    private val pathLens = Path.long().of("id")
//    private val nameLens = MultipartFormField.string().required("name")
//    private val descriptionLens = MultipartFormField.string().required("description")
//
//}
