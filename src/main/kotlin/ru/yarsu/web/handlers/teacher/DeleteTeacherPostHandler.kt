package ru.yarsu.web.handlers.teacher

import org.http4k.core.*
import org.http4k.core.body.form
import ru.yarsu.db.UserData
import java.nio.file.Files
import java.nio.file.Paths

class DeleteTeacherPostHandler(
    private val teachers: UserData
) : HttpHandler {

    override fun invoke(request: Request): Response {
        val teacherId = request.form("teacherId")?.toIntOrNull()
            ?: return Response(Status.BAD_REQUEST).body("Некорректный ID преподавателя")

        val existingTeacher = teachers.getTeacherById(teacherId)
            ?: return Response(Status.NOT_FOUND).body("Преподаватель не найден")

//        existingTeacher.avatarFileName?.forEach { filename ->
//            val path = Paths.get("src/main/resources/ru/yarsu/public/img").resolve(filename)
//            if (Files.exists(path)) {
//                Files.delete(path)
//                println("Удалён файл: $filename")
//            }
//        }

        teachers.removeTeacherRoleById(teacherId)

        return Response(Status.FOUND).header("Location", "/teachers")
    }
}
