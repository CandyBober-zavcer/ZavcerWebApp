package ru.yarsu.db

import ru.yarsu.web.domain.article.*
import java.time.LocalDate
import java.time.LocalTime

class TeachersData {
    fun fillTeachers(): List<Teacher> {
        return listOf(
            Teacher(
                id = 1L,
                fullName = "Серёга Пират",
                photoUrl = listOf("https://i.ytimg.com/vi/GXHsLWlU2jQ/maxresdefault.jpg?sqp=-oaymwEmCIAKENAF8quKqQMa8AEB-AHUBoAC4AOKAgwIABABGFUgEyh_MA8=&rs=AOn4CLBCwS6JXQfNbcx2PlwGzzgO3MZmFw"),
                shortDescription = "Барабанщик и наставник по тяжёлой музыке",
                experienceInfo = ExperienceInfo(
                    experienceYears = 12,
                    education = "Академия современной музыки",
                    styles = listOf(MusicStyle.METAL, MusicStyle.PUNK),
                    minStudentAge = 14
                ),
                roles = PersonRole(
                    owners = listOf(2001L),
                    administrators = listOf(2002L)
                ),
                address = Location("ул. Ударников, д. 3"),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 15) to listOf(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                        LocalDate.of(2025, 5, 16) to listOf(LocalTime.of(13, 0))
                    )
                ),
                instruments = listOf(Instrument.DRUMS)
            ),
            Teacher(
                id = 2L,
                fullName = "Шаман",
                photoUrl = listOf("http://cdn.iz.ru/sites/default/files/styles/900x506/public/news-2023-06/20230314_gaf_rk39_016.jpg?itok=BC-92G5P"),
                shortDescription = "Вокалист и преподаватель сценического искусства",
                experienceInfo = ExperienceInfo(
                    experienceYears = 8,
                    education = "ГИТИС",
                    styles = listOf(MusicStyle.CLASSICAL, MusicStyle.POP),
                    minStudentAge = 12
                ),
                roles = PersonRole(
                    owners = listOf(2010L),
                    administrators = listOf(2011L)
                ),
                address = Location("пр. Звука, д. 7"),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 17) to listOf(LocalTime.of(14, 0), LocalTime.of(15, 0))
                    )
                ),
                instruments = listOf(Instrument.KEYBOARD)
            ),
            Teacher(
                id = 3L,
                fullName = "Филипп Киркоров",
                photoUrl = listOf("https://images.thevoicemag.ru/upload/img_cache/d2a/d2a4b203d393bf7a8f98e3d52a7ef85f_ce_1628x1080x0x228_cropped_1332x888.jpg"),
                shortDescription = "Звезда поп-сцены, тренер артистизма",
                experienceInfo = ExperienceInfo(
                    experienceYears = 25,
                    education = "Консерватория им. Чайковского",
                    styles = listOf(MusicStyle.POP),
                    minStudentAge = 16
                ),
                roles = PersonRole(
                    owners = listOf(2020L),
                    administrators = listOf()
                ),
                address = Location("ул. Гламура, д. 9"),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 20) to listOf(LocalTime.of(11, 0))
                    )
                ),
                instruments = listOf(Instrument.VOCAL)
            ),
            Teacher(
                id = 4L,
                fullName = "Виктор Цой",
                photoUrl = listOf("https://static.life.ru/posts/2018/05/1120502/289e5df8ca71d8a41a5af98958f32b2b.jpg"),
                shortDescription = "Легенда рока, обучает гитаре и композиции",
                experienceInfo = ExperienceInfo(
                    experienceYears = 18,
                    education = "Школа Рок-музыки",
                    styles = listOf(MusicStyle.ROCK),
                    minStudentAge = 13
                ),
                roles = PersonRole(
                    owners = listOf(2030L),
                    administrators = listOf(2031L)
                ),
                address = Location("ул. Перемен, д. 1"),
                schedule = Schedule(
                    availability = mapOf(
                        LocalDate.of(2025, 5, 18) to listOf(LocalTime.of(10, 0), LocalTime.of(12, 0))
                    )
                ),
                instruments = listOf(Instrument.ACOUSTIC_GUITAR, Instrument.ELECTRIC_GUITAR)
            )
        )
    }
}
