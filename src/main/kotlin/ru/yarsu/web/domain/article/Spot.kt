package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.enums.DistrictEnums

/**
 * Класс, представляющий место (спот) для репетиций или выступлений.
 *
 * @property id Уникальный идентификатор места
 * @property name Название места
 * @property price Стоимость аренды места
 * @property hasDrums Наличие барабанной установки
 * @property guitarAmps Количество гитарных усилителей
 * @property bassAmps Количество басовых усилителей
 * @property description Описание места
 * @property address Адрес места
 * @property district Район, в котором находится место
 * @property images Список URL-адресов изображений места
 * @property schedule Словарь, отражающий занятость места,
 *                   где ключ — дата в формате "дд-мм-гггг",
 *                   а значение — список занятых временных слотов в формате "чч:мм"
 * @property owners Список идентификаторов владельцев места
 */
data class Spot(
    var id: Int = -1,
    var name: String = "",
    var price: Int = 0,
    var hasDrums: Boolean = false,
    var guitarAmps: Int = 0,
    var bassAmps: Int = 0,
    var description: String = "",
    var address: String = "",
    var district: DistrictEnums = DistrictEnums.UNKNOWN,
    var images: List<String> = emptyList(),
    var schedule: Map<String, List<String>> = emptyMap(), // DayOccupation?
    var owners: List<Int> = emptyList(),
)