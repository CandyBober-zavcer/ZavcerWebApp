package ru.yarsu.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.date
import ru.yarsu.db.tables.manyToMany.SpotsDays
import ru.yarsu.db.tables.manyToMany.UsersDays

object DayOccupations : IntIdTable() {
    val day = date("day").uniqueIndex()
}

class DayOccupationLine(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<DayOccupationLine>(DayOccupations)

    var day by DayOccupations.day
    val hours by HourOccupationLine referrersOn HourOccupations.day

    fun isFromUser(): Boolean {
        return UsersDays.select (UsersDays.day eq id ).count() > 0
    }
    fun isFromSpot(): Boolean {
        return SpotsDays.select ( SpotsDays.day eq id ).count() > 0
    }

    fun getUser(): UserLine? {
        return UsersDays
            .innerJoin(Users)
            .select ( UsersDays.day eq id )
            .limit(1)
            .map { UserLine.wrapRow(it) }
            .firstOrNull()
    }
    fun getSpot(): SpotLine? {
        return SpotsDays
            .innerJoin(Spots)
            .select ( SpotsDays.day eq id )
            .limit(1)
            .map { SpotLine.wrapRow(it) }
            .firstOrNull()
    }

    fun getSource(): Any? = getUser() ?: getSpot()
}
