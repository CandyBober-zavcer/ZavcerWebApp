package ru.yarsu.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.yarsu.db.tables.SpotLine.Companion.SEPARATOR
import ru.yarsu.db.tables.manyToMany.UsersDays
import ru.yarsu.db.tables.manyToMany.UsersSpots

object Users : IntIdTable() {
    val name = varchar("name", 50)
    val tg_name = varchar("tg_name", 50)
    val password = varchar("password", 50)
    val description = varchar("description", 50)
    val images = text("images").nullable()
    val roles = varchar("roles", 50)
}

class UserLine(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserLine>(Users)

    var name by Users.name
    var tg_name by Users.tg_name
    var password by Users.password
    var description by Users.description
    var roles by Users.roles
    var twoWeekOccupation by DayOccupationLine via UsersDays
    var spots by SpotLine via UsersSpots

    var images by Users.images.transform(
        { a -> a?.joinToString(SEPARATOR) },
        { str -> str?.split(SEPARATOR)?.toTypedArray() },
    )
}
