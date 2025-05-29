package ru.yarsu.web.domain.article

/**
 * Класс представляет музыкальную студию с её характеристиками и расписанием.
 *
 * @property id Уникальный идентификатор студии.
 * @property name Название студии.
 * @property description Краткое описание студии.
 * @property avatarFileName Путь до фотографии.
 * @property roles Роли пользователей (владельцы и администраторы).
 * @property location Адрес расположения студии.
 * @property capacity Вместимость студии (количество человек).
 * @property areaSquareMeters Площадь студии в квадратных метрах.
 * @property pricePerHour Цена за час аренды.
 * @property minBookingTimeHours Минимальное время бронирования в часах.
 * @property equipment Список доступного музыкального оборудования.
 * @property schedule Расписание доступности студии.
 */
data class Studio(
    val id: Long,
    val name: String,
    val description: String?,
    val avatarFileName: List<String>?,
    val roles: PersonRole,
    val location: Location,
    val capacity: Int,
    val areaSquareMeters: Double,
    val pricePerHour: Double,
    val minBookingTimeHours: Double,
    val equipment: List<Instrument>,
    val schedule: Schedule,
)
