package ru.yarsu.web.domain

enum class RoleEnums(
    val id: Int,
    val role: String,
) {
    ADMIN(0, "Админ"),
    OWNER(1, "Владелец точек"),
    TEACHER(2, "Преподаватель"),
    USER(3, "Пользователь"),
    ANONYMOUS(4, "Незарегистрирован"),
    ;

    companion object : EnumFinder<Int, RoleEnums>(entries.associateBy { it.id })
}

abstract class EnumFinder<V, E>(
    private val valueMap: Map<V, E>,
) {
    fun from(value: V) = valueMap[value]
}
