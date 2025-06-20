package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.core.body.form
import ru.yarsu.db.DatabaseController

class DeleteTeacherPostHandler(
    private val databaseController: DatabaseController,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val teacherId =
            request.form("teacherId")?.toIntOrNull()
                ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        val existingTeacher =
            databaseController.getTeacherById(teacherId)
                ?: return Response(Status.NOT_FOUND).body("Преподаватель не найден")

//        existingTeacher.avatarFileName?.forEach { filename ->
//            val path = Paths.get("src/main/resources/ru/yarsu/public/img").resolve(filename)
//            if (Files.exists(path)) {
//                Files.delete(path)
//                println("Удалён файл: $filename")
//            }
//        }

        databaseController.removeTeacherRoleById(teacherId)

        return Response(Status.FOUND).header("Location", "/teachers")
    }
}
