package ru.yarsu.web.domain.article

/**
 * Класс, представляющий преподавателя, его специализацию и доступность.
 *
 * @property fullName Полное имя преподавателя.
 * @property shortDescription Краткое описание (например, специализация или должность).
 * @property experienceInfo Информация об опыте преподавателя.
 * @property roles Роли пользователей (владельцы и администраторы).
 * @property address Адрес, по которому проводятся занятия.
 * @property schedule Расписание доступности по датам и временам (дата → список временных слотов).
 * @property instruments Список музыкальных инструментов, которым обучает преподаватель.
 */
data class Teacher(
    val fullName: String,
    val shortDescription: String,
    val experienceInfo: ExperienceInfo,
    val roles: PersonRole,
    val address: Location,
    val schedule: Schedule,
    val instruments: List<Instrument>
)

/**
 * Класс, содержащий информацию об опыте и квалификации преподавателя.
 *
 * @property experienceYears Количество лет опыта преподавания.
 * @property education Образование преподавателя.
 * @property styles Стили музыки, с которыми работает преподаватель (например, классика, джаз).
 * @property minStudentAge Минимальный возраст ученика, с которым работает преподаватель.
 */
data class ExperienceInfo(
    val experienceYears: Int,
    val education: String,
    val styles: List<MusicStyle>,
    val minStudentAge: Int
)
