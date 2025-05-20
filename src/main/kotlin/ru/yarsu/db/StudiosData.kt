package ru.yarsu.db

import ru.yarsu.web.domain.article.*
import java.time.LocalDate
import java.time.LocalTime

class StudiosData {
    private val studios: MutableList<Studio> = fillStudios().toMutableList()

    fun getAllStudios(): List<Studio> = studios

    fun getStudioById(id: Long): Studio? = studios.find { it.id == id }

    fun addStudio(studio: Studio): Boolean {
        if (studios.any { it.id == studio.id }) return false
        studios.add(studio)
        return true
    }

    fun removeStudio(id: Long): Boolean {
        return studios.removeIf { it.id == id }
    }

    fun updateStudio(updatedStudio: Studio): Boolean {
        val index = studios.indexOfFirst { it.id == updatedStudio.id }
        return if (index != -1) {
            studios[index] = updatedStudio
            true
        } else {
            false
        }
    }

    private fun fillStudios(): List<Studio> {
        return listOf(
            Studio(
                id = 1L,
                name = "Музыкальная студия №1",
                description = "Уютная студия с современным оборудованием.",
                photoUrl = listOf("https://avatars.mds.yandex.net/i?id=29d6e211074f66b4e77ee6e545ea34f1ed99ba33-6955035-images-thumbs&n=13"),
                roles = PersonRole(
                    owners = listOf(1001L),
                    administrators = listOf(1002L, 1003L)
                ),
                location = Location("ул. Музыкальная, д. 5"),
                capacity = 4,
                areaSquareMeters = 30.0,
                pricePerHour = 1500.0,
                minBookingTimeHours = 1.0,
                equipment = listOf(
                    Instrument.ELECTRIC_GUITAR,
                    Instrument.KEYBOARD,
                    Instrument.DJEMBE
                ),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 15) to listOf(
                            LocalTime.of(10, 0),
                            LocalTime.of(11, 0),
                            LocalTime.of(12, 0)
                        ),
                        LocalDate.of(2025, 5, 16) to listOf(
                            LocalTime.of(14, 0),
                            LocalTime.of(15, 0)
                        )
                    )
                )

            ),
            Studio(
                id = 2L,
                name = "Акустика+",
                description = "Студия для сольных исполнений и вокала.",
                photoUrl = listOf(
                    "https://yandex-images.clstorage.net/5Ohv12B14/43509b3HrQ9/PaYSGRP-VZKqw8MbHiMUPS6I2O5PKzaYvRPrIM3lKBkomZoXutd3QJO-hipIDtAGIBBI01bXRnSnm-GdsEwgrCkiAQiDOcDG-AQWGR7iVLp52BypWaoh3i2E6eWaJ-oNKgIVCnIL-MuedVq1VckGBCCQmkBHRwl6YcYiF-FIcDv7aBocFifamGPWRxYdJxn1cELa6t_vIhcwz6ZlxWAzELMFUQs_ifrM2nGVNBAGSkHo0l8eTwAPN_-CoNnnSb0wdiARmZ0rU1NsUQSQ1CMUf7cIUive7apfuI_j4AhpPFDywZeDsEk8SVq-X3eQBgOPZxdfG4EGxzFyCm4SL828OH0oG9kRYZJX55oF0FcvX_O8hlihEWnonj-Hq6wOIj4d-IPbyn5CeUIRt1H3lI0LySBbXBfLBM15ekKoWeEJfXO-KJAd3mQWFiuTDp5QZl-_sIgZZhSqZ9c-xGehTS5-3fDPW092yPFAmzzYuV_KzgGh35SfSo7Cdv_La19qRD19Oa_SHtWvHdJjmUldG6hZNzGF2qFdLapasgnm5ERv-R_3SJiOv0i0yN75nvOehEYGJRMd2YaBxbR8h6Vd4Qm58vToW5mcYV2RpRAPEt1un3E5yJihnWSs0XeOZCHOJz6ces_fRjbAdAQYflb3Xg4Dxe3dkJiHA8iztE7iXOyEPn03q1KQlqdcXihaAJ6b4JP2tw0Yr5yl4NCyR-goCGo_X3_FXowxSb4F0ntaNBqMisqt2t2TB47GfTPOItEphP41OCURGdrv2lFimYGZkysTdzzD02MRrC0ed0xu7QFpfBI0iVJC-Y32A5M-F3STBoICYFLXGcGCzjz6BGLX7ka3sfminx4f7F0Wr1lDGxYj1bYxAxphF-IhErEOZKeGb7RfOotQR3eB-YwfsxK7ns1ERWwd2dODSUJ-N40i1uKFMDp565Id3uVVGGPTR5eWa9Zz-0nS714polD1zmdtxQ"
                ),
                roles = PersonRole(
                    owners = listOf(1010L),
                    administrators = listOf(1011L)
                ),
                location = Location("пр. Независимости, д. 12"),
                capacity = 2,
                areaSquareMeters = 15.0,
                pricePerHour = 1000.0,
                minBookingTimeHours = 1.0,
                equipment = listOf(
                    Instrument.ACOUSTIC_GUITAR,
                    Instrument.TROMBONE
                ),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 17) to listOf(
                            LocalTime.of(9, 0),
                            LocalTime.of(10, 0),
                        ),
                        LocalDate.of(2025, 5, 18) to listOf(
                            LocalTime.of(13, 0),
                            LocalTime.of(14, 0)
                        )
                    )
                )
            ),

            Studio(
                id = 3L,
                name = "RockHouse",
                description = "Подходит для рок-групп. Отличная звукоизоляция и мощные усилители.",
                photoUrl = listOf("https://burobiz-a.akamaihd.net/uploads/images/44727/large_4503599658149925_8cbf.jpg"),
                roles = PersonRole(
                    owners = listOf(1020L),
                    administrators = listOf(1021L)
                ),
                location = Location("ул. Рокеров, д. 7"),
                capacity = 6,
                areaSquareMeters = 40.0,
                pricePerHour = 1800.0,
                minBookingTimeHours = 2.0,
                equipment = listOf(
                    Instrument.ELECTRIC_GUITAR,
                    Instrument.BASS,
                    Instrument.DRUMS
                ),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 19) to listOf(
                            LocalTime.of(16, 0),
                            LocalTime.of(17, 0),
                            LocalTime.of(18, 0)
                        ),
                        LocalDate.of(2025, 5, 20) to listOf(
                            LocalTime.of(10, 0),
                            LocalTime.of(11, 0)
                        )
                    )
                )
            ),

            Studio(
                id = 4L,
                name = "Jazz Loft",
                description = "Лофт в центре города. Отличный выбор для джаз-бендов.",
                photoUrl = listOf("https://theatre-light.ru/images/Gallery/2018/SmartAudio/smartaudio.jpg"),
                roles = PersonRole(
                    owners = listOf(1030L),
                    administrators = listOf(1031L, 1032L)
                ),
                location = Location("ул. Импровизации, д. 3"),
                capacity = 5,
                areaSquareMeters = 35.0,
                pricePerHour = 1600.0,
                minBookingTimeHours = 1.5,
                equipment = listOf(
                    Instrument.SAXOPHONE,
                    Instrument.KEYBOARD,
                    Instrument.DRUMS
                ),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 21) to listOf(
                            LocalTime.of(15, 0),
                            LocalTime.of(16, 30)
                        ),
                        LocalDate.of(2025, 5, 22) to listOf(
                            LocalTime.of(18, 0)
                        )
                    )
                )
            ),

            Studio(
                id = 5L,
                name = "Drum Base",
                description = "Специализирована под барабанщиков. Прочная конструкция, мощные томы.",
                photoUrl = listOf("https://i.pinimg.com/736x/d3/82/17/d3821793ee346a0778b3066317793694.jpg"),
                roles = PersonRole(
                    owners = listOf(1040L),
                    administrators = emptyList()
                ),
                location = Location("ул. Ударников, д. 9"),
                capacity = 1,
                areaSquareMeters = 10.0,
                pricePerHour = 800.0,
                minBookingTimeHours = 0.5,
                equipment = listOf(
                    Instrument.DRUMS
                ),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 23) to listOf(
                            LocalTime.of(8, 0),
                            LocalTime.of(8, 30),
                            LocalTime.of(9, 0)
                        ),
                        LocalDate.of(2025, 5, 24) to listOf(
                            LocalTime.of(17, 0),
                            LocalTime.of(17, 30),
                            LocalTime.of(18, 0)
                        )
                    )
                )
            )
        )
    }
}
