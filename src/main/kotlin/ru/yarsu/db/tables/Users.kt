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
    val phone = varchar("phone", 50)
    val experience = integer("experience")
    val abilities = varchar("abilities", 50) // по аналогии с roles
    val price = integer("price")
    val description = varchar("description", 50)
    val address = varchar("address", 50)
    val district = integer("district")
    val images = text("images")
    val roles = varchar("roles", 50)
}

class UserLine(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserLine>(Users)

    var name by Users.name
    var tg_name by Users.tg_name
    var password by Users.password
    var phone by Users.phone
    var experience by Users.experience
    var abilities by Users.abilities
    var price by Users.price
    var description by Users.description
    var address by Users.address
    var district by Users.district
    var roles by Users.roles
    var twoWeekOccupation by DayOccupationLine via UsersDays
    var spots by SpotLine via UsersSpots

    var images by Users.images.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).toTypedArray() },
    )
}
