package ru.yarsu.web.domain.enums

enum class RoleEnums(
    val id: Int,
    val role: String,
) {
    ADMIN(0, "Админ"),
    OWNER(1, "Владелец точек"),
    TEACHER(2, "Преподаватель"),
    USER(3, "Пользователь"),
    ANONYMOUS(4, "Незарегистрирован"),
    PENDING_TEACHER(5, "Ожидает подтверждения преподавателя"),
    PENDING_OWNER(6, "Ожидает подтверждения владельца"),
    ;

    companion object : EnumFinder<Int, RoleEnums>(entries.associateBy { it.id })
}
