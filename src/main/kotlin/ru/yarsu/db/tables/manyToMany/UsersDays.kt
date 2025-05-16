package ru.yarsu.db.tables.manyToMany

import org.jetbrains.exposed.sql.Table
import ru.yarsu.db.tables.DayOccupations
import ru.yarsu.db.tables.Users

object UsersDays : Table() {
    val user = reference("user", Users)
    val day = reference("day", DayOccupations)

    override val primaryKey = PrimaryKey(user, day)
}
