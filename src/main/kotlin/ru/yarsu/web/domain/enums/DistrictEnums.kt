package ru.yarsu.web.domain.enums

enum class DistrictEnums(
    val id: Int,
    val district: String,
) {
    DZERZHINSKY(0, "Дзержинский"),
    ZAVOLZHSKY(1, "Заволжский"),
    KIROVSKY(2, "Кировский"),
    KRASNOPEREKOPSKY(3, "Красноперекопский"),
    LENINSKY(4, "Ленинский"),
    FRUNZENSKY(5, "Фрунзенский"),
    UNKNOWN(-1, ""),
    ;

    companion object : EnumFinder<Int, DistrictEnums>(entries.associateBy { it.id })
}
