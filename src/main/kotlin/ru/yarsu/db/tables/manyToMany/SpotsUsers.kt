package ru.yarsu.db.tables.manyToMany

import org.jetbrains.exposed.sql.Table
import ru.yarsu.db.tables.Spots
import ru.yarsu.db.tables.Users

object SpotsUsers : Table() {
    val spot = reference("spot", Spots)
    val user = reference("user", Users)

    override val primaryKey = PrimaryKey(spot, user)
}