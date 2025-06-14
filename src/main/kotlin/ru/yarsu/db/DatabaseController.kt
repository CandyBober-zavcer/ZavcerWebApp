package ru.yarsu.db

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.databasecontrollers.OccupationsController
import ru.yarsu.db.databasecontrollers.SpotsController
import ru.yarsu.db.databasecontrollers.UsersController
import ru.yarsu.db.tables.*
import ru.yarsu.db.tables.manyToMany.SpotsDays
import ru.yarsu.db.tables.manyToMany.UsersDays
import ru.yarsu.db.tables.manyToMany.UsersSpots
import ru.yarsu.web.domain.classes.Spot
import ru.yarsu.web.domain.classes.DayOccupation
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.classes.`interface`.PaidPlace
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.models.telegram.TelegramUser

class DatabaseController {
    /**
     * Создаёт таблицы в базе данных.
     */
    fun init() {
        transaction {
            SchemaUtils.create(DayOccupations, HourOccupations, Spots, Users, SpotsDays, UsersDays, UsersSpots, UsersAbilities)
        }
    }

    /**
     * Сносит БД к чертям.
     */
    fun dropTables() {
        transaction {
            SchemaUtils.drop(DayOccupations, HourOccupations, Spots, Users, SpotsDays, UsersDays, UsersSpots)
        }
    }

    // Работа с Users
    fun getTeachersByPage(
        page: Int,
        limit: Int,
        abilityIds: List<Int> = AbilityEnums.entries.map { it.id },
        districtIds: List<Int> = DistrictEnums.entries.map { it.id },
        priceMin: Int = 0,
        priceMax: Int = Int.MAX_VALUE,
        experienceMin: Int = 0,
        sortByNearest: Boolean = false
    ): Pair<List<User>, Int> = UsersController().getTeachersByPage(page, limit, abilityIds, districtIds, priceMin, priceMax, experienceMin, sortByNearest)

    fun getUserById(id: Int): User? = UsersController().getUserById(id)

    fun getUserByEmail(email: String): User? = UsersController().getUserByEmail(email)

    fun getUserByLogin(login: String): User? = UsersController().getUserByLogin(login)

    fun getTeacherById(id: Int): User? = UsersController().getTeacherById(id)

    fun getTeacherByIdIfRolePendingTeacher(id: Int): User? = UsersController().getTeacherByIdIfRolePendingTeacher(id)

    fun getUserIfNotTeacher(id: Int): User? = UsersController().getUserIfNotTeacher(id)

    fun getAllUsersByRole(role: Int): List<User> = UsersController().getAllUsersByRole(role)

    fun updateTeacherRequest(id: Int, accept: Boolean): Boolean = UsersController().updateTeacherRequest(id, accept)

    fun removeTeacherRoleById(id: Int): Boolean = UsersController().removeTeacherRoleById(id)

    fun confirmUser(userId: Int): Boolean = UsersController().confirmUser(userId)

    fun updateUserPassword(
        userId: Int,
        newPassword: String,
    ): Boolean = UsersController().updateUserPassword(userId, newPassword)

    fun findOrCreateTelegramUser(telegramData: TelegramUser): User = UsersController().findOrCreateTelegramUser(telegramData)

    fun attachTelegram(
        userId: Int,
        telegramId: Long,
    ): Boolean = UsersController().attachTelegram(userId, telegramId)

    fun insertUser(user: User): Int = UsersController().insertUser(user)

    fun updateUserInfo(
        id: Int,
        data: User,
    ): Boolean = UsersController().updateUserInfo(id, data)

    fun userAddSpot(
        userId: Int,
        spotId: Int,
    ): Boolean = UsersController().addSpot(userId, spotId)

    fun userAddSpot(
        userId: Int,
        spotIds: List<Int>,
    ): Boolean = UsersController().addSpot(userId, spotIds)

    fun userRemoveSpot(
        userId: Int,
        spotId: Int,
    ): Boolean = UsersController().removeSpot(userId, spotId)

    fun userRemoveSpot(
        userId: Int,
        spotIds: List<Int>,
    ): Boolean = UsersController().removeSpot(userId, spotIds)

    fun userAddDayOccupation(
        userId: Int,
        dayId: Int,
    ): Boolean = UsersController().addDayOccupation(userId, dayId)

    fun userAddDayOccupation(
        userId: Int,
        daysId: List<Int>,
    ): Boolean = UsersController().addDayOccupation(userId, daysId)

    fun userRemoveDayOccupation(
        userId: Int,
        dayId: Int,
    ): Boolean = UsersController().removeDayOccupation(userId, dayId)

    fun userRemoveDayOccupation(
        userId: Int,
        daysId: List<Int>,
    ): Boolean = UsersController().removeDayOccupation(userId, daysId)

    fun verifyPassword(
        user: User,
        password: String,
    ): Boolean = UsersController().verifyPassword(user, password)

    fun getUserSchedule(userId: Int): List<Pair<String, PaidPlace>> {
        val schedule = mutableListOf<Pair<String, PaidPlace>>()

        transaction {
            val user = UserLine.findById(userId)
            user?.occupiedHours?.forEach {
                var time = ""
                time += "${it.hour}:00 ${it.day.day}"
                val place = it.day.getSource()
                if (place is SpotLine) {
                    schedule.add(Pair(time, SpotsController().packSpot(place)))
                } else if (place is UserLine) {
                    schedule.add(Pair(time, UsersController().packUser(place)))
                }
            }
        }
        return schedule
    }

    // Работа со Spots
    fun getSpotsByPage(
        page: Int,
        limit: Int,
        drums: Boolean = false,
        guitarAmps: Int = 0,
        bassAmps: Int = 0,
        districtList: List<Int> = DistrictEnums.entries.map { it.id },
        priceLow: Int = 0,
        priceHigh: Int = Int.MAX_VALUE,
        sortByNearest: Boolean = false
    ): Pair<List<Spot>, Int> = SpotsController().getSpotsByPage(page, limit, drums, guitarAmps, bassAmps, districtList, priceLow, priceHigh, sortByNearest)

    fun getAllSpots(): List<Spot> = SpotsController().getAllSpots()

    fun getSpotById(id: Int): Spot? = SpotsController().getSpotById(id)

    fun insertSpot(spot: Spot): Int = SpotsController().insertSpot(spot)

    fun deleteSpot(id: Int): Boolean = SpotsController().deleteSpot(id)

    fun updateSpotInfo(
        id: Int,
        data: Spot,
    ): Boolean = SpotsController().updateSpotInfo(id, data)

    fun spotAddOwner(
        spotId: Int,
        userId: Int,
    ): Boolean = SpotsController().addOwner(spotId, userId)

    fun spotAddOwner(
        spotId: Int,
        userIds: List<Int>,
    ): Boolean = SpotsController().addOwner(spotId, userIds)

    fun spotRemoveOwner(
        spotId: Int,
        userId: Int,
    ): Boolean = SpotsController().removeOwner(spotId, userId)

    fun spotRemoveOwner(
        spotId: Int,
        userIds: List<Int>,
    ): Boolean = SpotsController().removeOwner(spotId, userIds)

    fun spotAddDayOccupation(
        spotId: Int,
        dayId: Int,
    ): Boolean = SpotsController().addDayOccupation(spotId, dayId)

    fun spotAddDayOccupation(
        spotId: Int,
        daysId: List<Int>,
    ): Boolean = SpotsController().addDayOccupation(spotId, daysId)

    fun spotRemoveDayOccupation(
        spotId: Int,
        dayId: Int,
    ): Boolean = SpotsController().removeDayOccupation(spotId, dayId)

    fun spotRemoveDayOccupation(
        spotId: Int,
        daysId: List<Int>,
    ): Boolean = SpotsController().removeDayOccupation(spotId, daysId)

    // Работа с DayOccupations и HourOccupations
    fun getDayOccupationById(id: Int): DayOccupation = OccupationsController().getDayOccupationById(id)

    fun getListDayOccupation(ids: List<Int>): List<DayOccupation> = OccupationsController().getListDayOccupation(ids)

    fun insertDayOccupation(date: LocalDate): Int = OccupationsController().insertDayOccupation(date)

    fun insertHourOccupation(
        time: Int,
        dateId: Int,
    ): Int = OccupationsController().insertHourOccupation(time, dateId)

    fun insertHourOccupation(
        time: Int,
        dateTarget: LocalDate,
    ): Int = OccupationsController().insertHourOccupation(time, dateTarget)

    fun insertHourGroupOccupation(
        times: List<Int>,
        dateId: Int,
    ): List<Int> = OccupationsController().insertHourGroupOccupation(times, dateId)

    fun insertHourGroupOccupation(
        times: List<Int>,
        dateTarget: LocalDate,
    ): List<Int> = OccupationsController().insertHourGroupOccupation(times, dateTarget)

    fun occupyHour(
        date: LocalDate,
        targetHour: Int,
        userId: Int,
    ): Boolean = OccupationsController().occupyHour(date, targetHour, userId)

    fun unoccupyHour(
        dayId: Int,
        targetHour: Int,
        userId: Int,
    ): Boolean = OccupationsController().unoccupyHour(dayId, targetHour, userId)

    fun getOwnerByIdIfRolePendingOwner(id: Int): User? = UsersController().getOwnerByIdIfRolePendingOwner(id)

    fun getUserIfNotOwner(id: Int): User? = UsersController().getUserIfNotOwner(id)

    fun acceptTeacherRequest(id: Int): Boolean = UsersController().acceptTeacherRequest(id)
    fun rejectTeacherRequest(id: Int): Boolean = UsersController().rejectTeacherRequest(id)
    fun acceptOwnerRequest(id: Int): Boolean = UsersController().acceptOwnerRequest(id)
    fun rejectOwnerRequest(id: Int): Boolean = UsersController().rejectOwnerRequest(id)

    fun getPendingTeachers(): List<User> = UsersController().getPendingTeachers()
    fun getPendingOwners(): List<User> = UsersController().getPendingOwners()

    fun addTeacherRoleById(id: Int): Boolean = UsersController().addTeacherRoleById(id)
    fun addOwnerRoleById(id: Int): Boolean = UsersController().addOwnerRoleById(id)
    fun removeOwnerRoleById(id: Int): Boolean = UsersController().removeOwnerRoleById(id)
    fun deleteUser(id: Int): Boolean = UsersController().deleteUser(id)
    fun getOwnerById(id: Int): User? = UsersController().getOwnerById(id)

    fun getAllUsers(): List<User> = UsersController().getAllUsers()
}
