package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.EnumFinder
import ru.yarsu.web.domain.RoleEnums

/**
 * Стили музыки, используемые для описания предпочтений преподавателя или студии.
 *
 * @property id Уникальный идентификатор стиля.
 * @property displayName Отображаемое имя стиля.
 */
enum class MusicStyle(val id: Int, private val displayName: String) {
    CLASSICAL(1, "Классическая музыка"),
    JAZZ(2, "Джаз"),
    ROCK(3, "Рок"),
    POP(4, "Поп"),
    BLUES(5, "Блюз"),
    FOLK(6, "Фолк"),
    ELECTRONIC(7, "Электронная музыка"),
    HIP_HOP(8, "Хип-хоп"),
    REGGAE(9, "Регги"),
    LATIN(10, "Латиноамериканская музыка"),
    FUNK(11, "Фанк"),
    METAL(12, "Метал"),
    COUNTRY(13, "Кантри"),
    RNB(14, "R&B"),
    EXPERIMENTAL(15, "Экспериментальная музыка"),
    PUNK(16, "Панк"),
    OTHER(16, "Другое");

    companion object {
        private val idMap = entries.associateBy { it.id }
        fun get(id: Int): MusicStyle? = idMap[id]
    }

    override fun toString(): String = displayName
}
