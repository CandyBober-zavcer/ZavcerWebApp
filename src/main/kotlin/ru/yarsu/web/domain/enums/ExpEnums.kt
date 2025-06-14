package ru.yarsu.web.domain.enums

enum class ExpEnums(
    val id: Int,
    val experience: String,
) {
    ANY(0, "Любой"),
    ONE(1, "От 1 года"),
    THREE(3, "От 3 лет"),
    FIVE(5, "От 5 лет"),
    TEN(10, "От 10 лет"),
    ;

    companion object : EnumFinder<Int, ExpEnums>(entries.associateBy { it.id })
}
