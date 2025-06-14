package ru.yarsu.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object HourOccupations : IntIdTable() {
    val hour = integer("hour")
    val occupation = reference("user", Users).nullable()
    val day = reference("day", DayOccupations)
}

class HourOccupationLine(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<HourOccupationLine>(HourOccupations)

    var hour by HourOccupations.hour
    var occupation by UserLine optionalReferencedOn HourOccupations.occupation
    var day by DayOccupationLine referencedOn HourOccupations.day
}
