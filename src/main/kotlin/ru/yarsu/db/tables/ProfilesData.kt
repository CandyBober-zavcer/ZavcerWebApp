package ru.yarsu.db.tables

import ru.yarsu.web.domain.article.*

class ProfilesData {
    fun fillProfiles(): List<Profile> {
        return listOf(
            Profile(
                id = 1001L,
                name = "Алексей Смирнов",
                description = "Гитарист с опытом более 10 лет, участник рок-группы.",
                instruments = listOf(Instrument.ELECTRIC_GUITAR, Instrument.BASS),
                styles = listOf(MusicStyle.ROCK, MusicStyle.METAL),
                avatarUrl = "https://example.com/avatars/alexey.jpg"
            ),
            Profile(
                id = 1002L,
                name = "Мария Иванова",
                description = "Вокалистка и автор песен в жанре поп и джаз.",
                instruments = listOf(Instrument.KEYBOARD),
                styles = listOf(MusicStyle.POP, MusicStyle.JAZZ),
                avatarUrl = "https://example.com/avatars/maria.jpg"
            ),
            Profile(
                id = 1003L,
                name = "Игорь Козлов",
                description = "Барабанщик, специализирующийся на живых выступлениях.",
                instruments = listOf(Instrument.DRUMS),
                styles = listOf(MusicStyle.ROCK, MusicStyle.FUNK),
                avatarUrl = "https://example.com/avatars/igor.jpg"
            ),
            Profile(
                id = 1004L,
                name = "Елена Ветрова",
                description = "Саксофонистка и преподаватель музыки.",
                instruments = listOf(Instrument.SAXOPHONE),
                styles = listOf(MusicStyle.JAZZ, MusicStyle.BLUES),
                avatarUrl = "https://example.com/avatars/elena.jpg"
            ),
            Profile(
                id = 1005L,
                name = "Никита Орлов",
                description = "Мультиинструменталист и саунд-продюсер.",
                instruments = listOf(Instrument.KEYBOARD, Instrument.DJEMBE, Instrument.ACOUSTIC_GUITAR),
                styles = listOf(MusicStyle.ELECTRONIC, MusicStyle.METAL),
                avatarUrl = "https://example.com/avatars/nikita.jpg"
            )
        )
    }
}
