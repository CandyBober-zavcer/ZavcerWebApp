package ru.yarsu.web.handlers.studio

import org.http4k.core.*
import org.http4k.core.body.form
import ru.yarsu.db.StudiosData
import ru.yarsu.web.templates.ContextAwareViewRender
import java.nio.file.Files
import java.nio.file.Paths

class DeleteStudioPostHandler (private val htmlView: ContextAwareViewRender, private val studios: StudiosData): HttpHandler {

    override fun invoke(request: Request): Response {
        val studioId = request.form("studioId")?.toLongOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID студии")

        val existingStudio = studios.getStudioById(studioId)
            ?: return Response(Status.NOT_FOUND).body("Студия не найдена")

//        existingStudio.avatarFileName?.forEach { filename ->
//            val path = Paths.get("src/main/resources/ru/yarsu/public/img").resolve(filename)
//            if (Files.exists(path)) {
//                Files.delete(path)
//            }
//        }

        studios.removeStudio(studioId)

        return Response(Status.FOUND).header("Location", "/studios")
    }

}