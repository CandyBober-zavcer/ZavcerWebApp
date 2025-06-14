package ru.yarsu.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.SpotLine.Companion.SEPARATOR
import ru.yarsu.db.tables.manyToMany.UsersDays
import ru.yarsu.db.tables.manyToMany.UsersSpots

object Users : IntIdTable() {
    val name = text("name")
    val login = text("login")
    val tg_id = long("tg_id")
    val password = text("password")
    val phone = text("phone")
    val experience = integer("experience")
    val price = integer("price")
    val description = text("description")
    val address = text("address")
    val district = integer("district")
    val images = text("images")
    val roles = text("roles")
    val isConfirmed = bool("isConfirmed")
}

object UsersAbilities : IntIdTable() {
    val ability = integer("ability")
    val user = reference("user_id", Users)
}

class UserLine(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserLine>(Users)

    var name by Users.name
    var tg_id by Users.tg_id
    var login by Users.login
    var password by Users.password
    var phone by Users.phone
    var experience by Users.experience
    val abilities by UserAbility referrersOn UsersAbilities.user
    var price by Users.price
    var description by Users.description
    var address by Users.address
    var district by Users.district
    var twoWeekOccupation by DayOccupationLine via UsersDays
    var roles by Users.roles
    var spots by SpotLine via UsersSpots
    val occupiedHours by HourOccupationLine optionalReferrersOn HourOccupations.occupation
    var isConfirmed by Users.isConfirmed

    var images by Users.images.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).toTypedArray() },
    )
}

class UserAbility(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserAbility>(UsersAbilities)

    var ability by UsersAbilities.ability
    var user by UserLine referencedOn UsersAbilities.user
}

fun UserLine.addAbilities(abilities: List<Int>) =
    transaction {
        abilities.forEach {
            UserAbility.new {
                ability = it
                user = this@addAbilities
            }
        }
    }

fun UserLine.withAbilities(abilities: List<Int>): UserLine {
    this.addAbilities(abilities)
    return this
}

fun UserLine.updateAbilities(newAbilityIds: List<Int>) {
    transaction {
        abilities.filter { it.ability !in newAbilityIds }.forEach { it.delete() }

        val existingIds = abilities.map { it.ability }.toSet()
        newAbilityIds.filterNot { it in existingIds }.forEach { abilityId ->
            UserAbility.new {
                user = this@updateAbilities
                ability = abilityId
            }
        }
    }
}