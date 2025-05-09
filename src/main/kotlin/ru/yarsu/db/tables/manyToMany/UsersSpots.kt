package ru.yarsu.db.tables.manyToMany

import org.jetbrains.exposed.sql.Table
import ru.yarsu.db.tables.Spots
import ru.yarsu.db.tables.Users

object UsersSpots : Table() {
    val user = reference("user", Users)
    val spot = reference("spot", Spots)

    override val primaryKey = PrimaryKey(user, spot)
}