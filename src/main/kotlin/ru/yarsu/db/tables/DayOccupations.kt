package ru.yarsu.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object DayOccupations : IntIdTable() {
    val day = date("day").uniqueIndex()
}

class DayOccupationLine(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<DayOccupationLine>(DayOccupations)

    var day by DayOccupations.day
    val hours by HourOccupationLine referrersOn HourOccupations.day
}
