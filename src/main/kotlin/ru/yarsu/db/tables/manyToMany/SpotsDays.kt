package ru.yarsu.db.tables.manyToMany

import org.jetbrains.exposed.sql.Table
import ru.yarsu.db.tables.DayOccupations
import ru.yarsu.db.tables.Spots

object SpotsDays : Table() {
    val spot = reference("spot", Spots)
    val day = reference("day", DayOccupations)

    override val primaryKey = PrimaryKey(spot, day)
}
