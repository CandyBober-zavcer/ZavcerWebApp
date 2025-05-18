package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.EnumFinder

enum class Instrument(val id: Int, private val displayName: String) {
    GUITAR(1, "Гитара"),
    ACOUSTIC_GUITAR(2, "Акустическая гитара"),
    ELECTRIC_GUITAR(3, "Электрогитара"),
    BASS(4, "Бас-гитара"),
    UKULELE(5, "Укулеле"),
    PIANO(6, "Пианино"),
    SYNTH(7, "Синтезатор"),
    KEYBOARD(8, "Клавиши"),
    ORGAN(9, "Орган"),
    DRUMS(10, "Барабаны"),
    CAJON(11, "Кахон"),
    DJEMBE(12, "Джембе"),
    PERCUSSION(13, "Перкуссия"),
    VIOLIN(14, "Скрипка"),
    VIOLA(15, "Альт"),
    CELLO(16, "Виолончель"),
    DOUBLE_BASS(17, "Контрабас"),
    FLUTE(18, "Флейта"),
    CLARINET(19, "Кларнет"),
    SAXOPHONE(20, "Саксофон"),
    OBOE(21, "Гобой"),
    TRUMPET(22, "Труба"),
    TROMBONE(23, "Тромбон"),
    FRENCH_HORN(24, "Валторна"),
    HARMONICA(25, "Губная гармоника"),
    ACCORDION(26, "Аккордеон"),
    VOCAL(27, "Вокал"),
    OPERA_VOCAL(28, "Оперный вокал"),
    JAZZ_VOCAL(29, "Джазовый вокал"),
    DJ(30, "DJ-пульт"),
    COMPUTER_MUSIC(31, "Компьютерная музыка"),
    OTHER(32, "Другое");

    companion object {
        private val idMap = entries.associateBy { it.id }
        fun get(id: Int): Instrument? = idMap[id]
    }

    override fun toString(): String = displayName
}
