package ru.yarsu.db

import ru.yarsu.web.domain.article.*
import java.time.LocalDate
import java.time.LocalTime

class StudiosData {
    private var nextId = 6L
    private val studios: MutableList<Studio> = fillStudios().toMutableList()

    fun getAllStudios(): List<Studio> = studios

    fun getStudioById(id: Long): Studio? = studios.find { it.id == id }

    fun getNextId(): Long = nextId++

    fun addStudio(studio: Studio) {
        studios.add(studio)
    }

    fun removeStudio(id: Long): Boolean = studios.removeIf { it.id == id }

    fun updateStudio(
        id: Long,
        newStudio: Studio,
    ): Boolean {
        val index = studios.indexOfFirst { it.id == id }
        return if (index != -1) {
            studios[index] = newStudio.copy(id = id)
            true
        } else {
            false
        }
    }

    private fun fillStudios(): List<Studio> =
        listOf(
            Studio(
                id = 1L,
                name = "Музыкальная студия №1",
                description = "Уютная студия с современным оборудованием.",
                avatarFileName = listOf("studio1.webp"),
                roles =
                    PersonRole(
                        owners = listOf(1001L),
                        administrators = listOf(1002L, 1003L),
                    ),
                location = Location("ул. Музыкальная, д. 5"),
                capacity = 4,
                areaSquareMeters = 30.0,
                pricePerHour = 1500.0,
                minBookingTimeHours = 1.0,
                equipment =
                    listOf(
                        Instrument.ELECTRIC_GUITAR,
                        Instrument.KEYBOARD,
                        Instrument.DJEMBE,
                    ),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 15) to
                                    listOf(
                                        LocalTime.of(10, 0),
                                        LocalTime.of(11, 0),
                                        LocalTime.of(12, 0),
                                    ),
                                LocalDate.of(2025, 5, 16) to
                                    listOf(
                                        LocalTime.of(14, 0),
                                        LocalTime.of(15, 0),
                                    ),
                            ),
                    ),
            ),
            Studio(
                id = 2L,
                name = "Акустика+",
                description = "Студия для сольных исполнений и вокала.",
                avatarFileName =
                    listOf(
                        "studio2.webp",
                    ),
                roles =
                    PersonRole(
                        owners = listOf(1010L),
                        administrators = listOf(1011L),
                    ),
                location = Location("пр. Независимости, д. 12"),
                capacity = 2,
                areaSquareMeters = 15.0,
                pricePerHour = 1000.0,
                minBookingTimeHours = 1.0,
                equipment =
                    listOf(
                        Instrument.ACOUSTIC_GUITAR,
                        Instrument.TROMBONE,
                    ),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 17) to
                                    listOf(
                                        LocalTime.of(9, 0),
                                        LocalTime.of(10, 0),
                                    ),
                                LocalDate.of(2025, 5, 18) to
                                    listOf(
                                        LocalTime.of(13, 0),
                                        LocalTime.of(14, 0),
                                    ),
                            ),
                    ),
            ),
            Studio(
                id = 3L,
                name = "RockHouse",
                description = "Подходит для рок-групп. Отличная звукоизоляция и мощные усилители.",
                avatarFileName = listOf("studio3.jpg"),
                roles =
                    PersonRole(
                        owners = listOf(1020L),
                        administrators = listOf(1021L),
                    ),
                location = Location("ул. Рокеров, д. 7"),
                capacity = 6,
                areaSquareMeters = 40.0,
                pricePerHour = 1800.0,
                minBookingTimeHours = 2.0,
                equipment =
                    listOf(
                        Instrument.ELECTRIC_GUITAR,
                        Instrument.BASS,
                        Instrument.DRUMS,
                    ),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 19) to
                                    listOf(
                                        LocalTime.of(16, 0),
                                        LocalTime.of(17, 0),
                                        LocalTime.of(18, 0),
                                    ),
                                LocalDate.of(2025, 5, 20) to
                                    listOf(
                                        LocalTime.of(10, 0),
                                        LocalTime.of(11, 0),
                                    ),
                            ),
                    ),
            ),
            Studio(
                id = 4L,
                name = "Jazz Loft",
                description = "Лофт в центре города. Отличный выбор для джаз-бендов.",
                avatarFileName = listOf("studio4.jpg"),
                roles =
                    PersonRole(
                        owners = listOf(1030L),
                        administrators = listOf(1031L, 1032L),
                    ),
                location = Location("ул. Импровизации, д. 3"),
                capacity = 5,
                areaSquareMeters = 35.0,
                pricePerHour = 1600.0,
                minBookingTimeHours = 1.5,
                equipment =
                    listOf(
                        Instrument.SAXOPHONE,
                        Instrument.KEYBOARD,
                        Instrument.DRUMS,
                    ),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 21) to
                                    listOf(
                                        LocalTime.of(15, 0),
                                        LocalTime.of(16, 30),
                                    ),
                                LocalDate.of(2025, 5, 22) to
                                    listOf(
                                        LocalTime.of(18, 0),
                                    ),
                            ),
                    ),
            ),
            Studio(
                id = 5L,
                name = "Drum Base",
                description = "Специализирована под барабанщиков. Прочная конструкция, мощные томы.",
                avatarFileName = listOf("studio5.jpg"),
                roles =
                    PersonRole(
                        owners = listOf(1040L),
                        administrators = emptyList(),
                    ),
                location = Location("ул. Ударников, д. 9"),
                capacity = 1,
                areaSquareMeters = 10.0,
                pricePerHour = 800.0,
                minBookingTimeHours = 0.5,
                equipment =
                    listOf(
                        Instrument.DRUMS,
                    ),
                schedule =
                    Schedule(
                        availability =
                            mapOf(
                                LocalDate.of(2025, 5, 23) to
                                    listOf(
                                        LocalTime.of(8, 0),
                                        LocalTime.of(8, 30),
                                        LocalTime.of(9, 0),
                                    ),
                                LocalDate.of(2025, 5, 24) to
                                    listOf(
                                        LocalTime.of(17, 0),
                                        LocalTime.of(17, 30),
                                        LocalTime.of(18, 0),
                                    ),
                            ),
                    ),
            ),
        )
}
