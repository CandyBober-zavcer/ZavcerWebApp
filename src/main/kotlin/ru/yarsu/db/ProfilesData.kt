package ru.yarsu.db

import ru.yarsu.web.domain.article.*

class ProfilesData {
    private val profiles: MutableList<Profile> = fillProfiles().toMutableList()

    fun getAllProfiles(): List<Profile> = profiles

    fun getProfileById(id: Long): Profile? = profiles.find { it.id == id }

    fun addProfile(profile: Profile): Boolean {
        if (profiles.any { it.id == profile.id }) return false
        profiles.add(profile)
        return true
    }

    fun removeProfile(id: Long): Boolean {
        return profiles.removeIf { it.id == id }
    }

    fun updateProfile(updatedProfile: Profile): Boolean {
        val index = profiles.indexOfFirst { it.id == updatedProfile.id }
        return if (index != -1) {
            profiles[index] = updatedProfile
            true
        } else {
            false
        }
    }

    private fun fillProfiles(): List<Profile> {
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
            ),
            Profile(
                id = 1831874252,
                name = "Смирнов Александр",
                description = "Опытный клавишник с тонким чувством ритма. Увлекается живыми акустическими сетами и джазовыми импровизациями на саксофоне. Работал с коллективами в стиле RnB и кантри.",
                instruments = listOf(Instrument.KEYBOARD, Instrument.CAJON, Instrument.SAXOPHONE),
                styles = listOf(MusicStyle.RNB, MusicStyle.COUNTRY),
                avatarUrl = "https://avatars.yandex.net/get-music-content/4716681/3ac31825.a.15129028-1/m1000x1000?webp=false"
            ),
            Profile(
                id = 777990904,
                name = "Казаков Пётр",
                description = "Гитарист с 10-летним стажем, специализирующийся на роке и металле. Играет как на акустике, так и на электрогитаре. Выступал на даче у Егора, дома у Кирилла, подвалах и переходах Ярославля.",
                instruments = listOf(Instrument.GUITAR, Instrument.ELECTRIC_GUITAR),
                styles = listOf(MusicStyle.METAL, MusicStyle.ROCK),
                avatarUrl = "https://steamuserimages-a.akamaihd.net/ugc/55829769501469083/D83ED851B2DB78DFA12514FE48E0355647C6A38C/?imw=512&amp;imh=357&amp;ima=fit&amp;impolicy=Letterbox&amp;imcolor=%23000000&amp;letterbox=true"
            )
        )
    }
}
