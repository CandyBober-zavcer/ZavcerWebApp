package ru.yarsu.db

import ru.yarsu.web.domain.article.*
import java.time.LocalDate
import java.time.LocalTime

class TeachersData {
    private var nextId = 6L
    private val teachers: MutableList<Teacher> = fillTeachers().toMutableList()

    fun getAllTeachers(): List<Teacher> = teachers

    fun getTeacherById(id: Long): Teacher? = teachers.find { it.id == id }

    fun getNextId(): Long = nextId++

    fun addTeacher(teacher: Teacher) {
        teachers.add(teacher)
    }

    fun removeTeacher(id: Long): Boolean = teachers.removeIf { it.id == id }

    fun updateTeacher(
        id: Long,
        newTeacher: Teacher,
    ): Boolean {
        val index = teachers.indexOfFirst { it.id == id }
        return if (index != -1) {
            teachers[index] = newTeacher.copy(id = id)
            true
        } else {
            false
        }
    }

    private fun fillTeachers(): List<Teacher> =
        listOf(
            Teacher(
                id = 1L,
                fullName = "Серёга Пират",
                avatarFileName = listOf("teacher1.jpg"),
                shortDescription = "Барабанщик и наставник по тяжёлой музыке",
                experienceInfo =
                    ExperienceInfo(
                        experienceYears = 12,
                        education = "Академия современной музыки",
                        styles = listOf(MusicStyle.METAL, MusicStyle.PUNK),
                        minStudentAge = 14,
                    ),
                roles =
                    PersonRole(
                        owners = listOf(2001L),
                        administrators = listOf(2002L),
                    ),
                address = Location("ул. Ударников, д. 3"),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 15) to listOf(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                                LocalDate.of(2025, 5, 16) to listOf(LocalTime.of(13, 0)),
                            ),
                    ),
                instruments = listOf(Instrument.DRUMS),
            ),
            Teacher(
                id = 2L,
                fullName = "Шаман",
                avatarFileName = listOf("teacher2.jpg"),
                shortDescription = "Вокалист и преподаватель сценического искусства",
                experienceInfo =
                    ExperienceInfo(
                        experienceYears = 8,
                        education = "ГИТИС",
                        styles = listOf(MusicStyle.CLASSICAL, MusicStyle.POP),
                        minStudentAge = 12,
                    ),
                roles =
                    PersonRole(
                        owners = listOf(2010L),
                        administrators = listOf(2011L),
                    ),
                address = Location("пр. Звука, д. 7"),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 17) to listOf(LocalTime.of(14, 0), LocalTime.of(15, 0)),
                            ),
                    ),
                instruments = listOf(Instrument.KEYBOARD),
            ),
            Teacher(
                id = 3L,
                fullName = "Филипп Киркоров",
                avatarFileName = listOf("teacher3.jpg"),
                shortDescription = "Звезда поп-сцены, тренер артистизма",
                experienceInfo =
                    ExperienceInfo(
                        experienceYears = 25,
                        education = "Консерватория им. Чайковского",
                        styles = listOf(MusicStyle.POP),
                        minStudentAge = 16,
                    ),
                roles =
                    PersonRole(
                        owners = listOf(2020L),
                        administrators = listOf(),
                    ),
                address = Location("ул. Гламура, д. 9"),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 20) to listOf(LocalTime.of(11, 0)),
                            ),
                    ),
                instruments = listOf(Instrument.VOCAL),
            ),
            Teacher(
                id = 4L,
                fullName = "Виктор Цой",
                avatarFileName = listOf("teacher4.jpg"),
                shortDescription = "Легенда рока, обучает гитаре и композиции",
                experienceInfo =
                    ExperienceInfo(
                        experienceYears = 18,
                        education = "Школа Рок-музыки",
                        styles = listOf(MusicStyle.ROCK),
                        minStudentAge = 13,
                    ),
                roles =
                    PersonRole(
                        owners = listOf(2030L),
                        administrators = listOf(2031L),
                    ),
                address = Location("ул. Перемен, д. 1"),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 18) to listOf(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                            ),
                    ),
                instruments = listOf(Instrument.ACOUSTIC_GUITAR, Instrument.ELECTRIC_GUITAR),
            ),
        )
}
