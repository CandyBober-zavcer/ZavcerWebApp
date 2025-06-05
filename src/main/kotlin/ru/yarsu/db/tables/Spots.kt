package ru.yarsu.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.yarsu.db.tables.manyToMany.SpotsDays
import ru.yarsu.db.tables.manyToMany.UsersSpots

object Spots : IntIdTable() {
    val name = varchar("name", 50)
    val price = integer("price")
    val hasDrums = bool("hasDrums")
    val guitarAmps = integer("guitarAmps")
    val bassAmps = integer("bassAmps")
    val description = varchar("description", 50)
    val address = varchar("address", 50)
    val district = integer("district")
    val images = text("images")
}

class SpotLine(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<SpotLine>(Spots) {
        const val SEPARATOR = ":"
    }

    var name by Spots.name
    var price by Spots.price
    var hasDrums by Spots.hasDrums
    var guitarAmps by Spots.guitarAmps
    var bassAmps by Spots.bassAmps
    var description by Spots.description
    var address by Spots.address
    var district by Spots.district
    var twoWeekOccupation by DayOccupationLine via SpotsDays
    var owners by UserLine via UsersSpots

    var images by Spots.images.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it }.toTypedArray() },
    )
}
