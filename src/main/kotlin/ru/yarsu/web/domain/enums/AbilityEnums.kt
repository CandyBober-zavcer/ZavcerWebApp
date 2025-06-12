package ru.yarsu.web.domain.enums

enum class AbilityEnums(
    val id: Int,
    val instrument: String,
) {
    GUITAR(0, "гитара"),
    BASS(1, "бас-гитара"),
    DRUMS(2, "барабаны"),
    VOICE(3, "вокал"),
    KEYBOARD(4, "клавишные"),
    ;

    companion object : EnumFinder<Int, AbilityEnums>(entries.associateBy { it.id })
}
