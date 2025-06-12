package ru.yarsu.web.domain.article

import java.time.LocalDate
import java.time.LocalTime

/**
 * Класс, описывающий роли пользователей, связанных с объектом (например, студией).
 *
 * @property owners Список Telegram ID владельцев.
 * @property administrators Список Telegram ID администраторов.
 */
data class PersonRole(
    val owners: List<Long>,
    val administrators: List<Long>,
)

/**
 * Класс, представляющий физическое местоположение.
 *
 * @property address Адрес в текстовом формате.
 */
data class Location(
    val address: String,
)

/**
 * Класс, описывающий расписание доступности.
 *
 * @property availability Карта, где ключ — дата (LocalDate), а значение — список времён (LocalTime),
 * указывающих на доступные слоты в течение этой даты.
 */
data class Schedule(
    val availability: Map<LocalDate, List<LocalTime>>,
)
