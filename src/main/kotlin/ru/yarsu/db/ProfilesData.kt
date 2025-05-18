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

    fun updateProfile(id: Long, newProfile: Profile): Boolean {
        val index = profiles.indexOfFirst { it.id == id }
        return if (index != -1) {
            profiles[index] = newProfile.copy(id = id)
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
                avatarFileName = "profile3.png"
            ),
            Profile(
                id = 1002L,
                name = "Мария Иванова",
                description = "Вокалистка и автор песен в жанре поп и джаз.",
                instruments = listOf(Instrument.KEYBOARD),
                styles = listOf(MusicStyle.POP, MusicStyle.JAZZ),
                avatarFileName = "profile4.webp"
            ),
            Profile(
                id = 1003L,
                name = "Игорь Козлов",
                description = "Барабанщик, специализирующийся на живых выступлениях.",
                instruments = listOf(Instrument.DRUMS),
                styles = listOf(MusicStyle.ROCK, MusicStyle.FUNK),
                avatarFileName = "profile5.webp"
            ),
            Profile(
                id = 1004L,
                name = "Елена Ветрова",
                description = "Саксофонистка и преподаватель музыки.",
                instruments = listOf(Instrument.SAXOPHONE),
                styles = listOf(MusicStyle.JAZZ, MusicStyle.BLUES),
                avatarFileName = ""
            ),
            Profile(
                id = 1005L,
                name = "Никита Орлов",
                description = "Мультиинструменталист и саунд-продюсер.",
                instruments = listOf(Instrument.KEYBOARD, Instrument.DJEMBE, Instrument.ACOUSTIC_GUITAR),
                styles = listOf(MusicStyle.ELECTRONIC, MusicStyle.METAL),
                avatarFileName = ""
            ),
            Profile(
                id = 1831874252,
                name = "Смирнов Александр",
                description = "Опытный клавишник с тонким чувством ритма. Увлекается живыми акустическими сетами и джазовыми импровизациями на саксофоне. Работал с коллективами в стиле RnB и кантри.",
                instruments = listOf(Instrument.KEYBOARD, Instrument.CAJON, Instrument.SAXOPHONE),
                styles = listOf(MusicStyle.RNB, MusicStyle.COUNTRY),
                avatarFileName = "profile1.jpg"
            ),
            Profile(
                id = 777990904,
                name = "Казаков Пётр",
                description = "Гитарист с 10-летним стажем, специализирующийся на роке и металле. Играет как на акустике, так и на электрогитаре. Выступал на даче у Егора, дома у Кирилла, подвалах и переходах Ярославля.",
                instruments = listOf(Instrument.GUITAR, Instrument.ELECTRIC_GUITAR),
                styles = listOf(MusicStyle.METAL, MusicStyle.ROCK),
                avatarFileName = "profile2.jpg"
            )
        )
    }
}
