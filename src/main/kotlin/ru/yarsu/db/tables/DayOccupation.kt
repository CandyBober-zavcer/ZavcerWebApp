package ru.yarsu.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object DayOccupations : IntIdTable() {
    val hour = integer("hour")
    val occupation = reference("user", Users).nullable()
}

class DayOccupationLine(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DayOccupationLine>(DayOccupations)

    var hour by DayOccupations.hour
    var occupation by UserLine optionalReferencedOn DayOccupations.occupation
}
