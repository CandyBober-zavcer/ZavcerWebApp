package ru.yarsu.web.domain.article

import ru.yarsu.web.domain.enums.*

/**
 * Класс, представляющий модель пользователя системы (музыканта или организатора).
 *
 * @property id Уникальный идентификатор пользователя
 * @property name Имя или псевдоним пользователя
 * @property tg_id ID пользователя в Telegram (0 если не привязан)
 * @property login Уникальный логин для входа в систему
 * @property password Пароль (должен храниться в хешированном виде)
 * @property phone Контактный телефон в формате строки
 * @property experience Опыт музыкальной деятельности в годах
 * @property abilities Набор музыкальных навыков/инструментов
 * @property price Примерная стоимость услуг (если пользователь исполнитель)
 * @property description Описание профиля, опыта и стилей
 * @property address Предпочитаемый район или адрес репетиций
 * @property district Район города (из справочника)
 * @property images Ссылки на фото/аватарки профиля
 * @property schedule График занятости пользователя (дата → список занятых слотов)
 * @property spots Список ID связанных спотов (если пользователь владелец)
 * @property roles Роли пользователя в системе (права доступа)
 * @property isConfirmed Подтверждён ли профиль администрацией
 */
data class UserModel(
    var id: Int = -1,
    var name: String = "",
    var tg_id: Long = 0L,
    var login: String = "",
    var password: String = "",
    var phone: String = "",
    var experience: Int = 0,
    var abilities: Set<AbilityEnums> = emptySet(),
    var price: Int = 0,
    var description: String = "",
    var address: String = "",
    var district: DistrictEnums = DistrictEnums.UNKNOWN,
    var images: List<String> = listOf(),
    var schedule: Map<Int, List<Int>> = emptyMap(),
    var spots: List<Int> = listOf(),
    var roles: Set<RoleEnums> = setOf(RoleEnums.ANONYMOUS),
    var isConfirmed: Boolean = true,
)