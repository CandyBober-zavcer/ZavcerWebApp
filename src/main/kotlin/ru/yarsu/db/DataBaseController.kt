package ru.yarsu.db

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.databasecontrollers.OccupationsController
import ru.yarsu.db.databasecontrollers.SpotsController
import ru.yarsu.db.databasecontrollers.UsersController
import ru.yarsu.db.tables.DayOccupations
import ru.yarsu.db.tables.HourOccupations
import ru.yarsu.db.tables.Spots
import ru.yarsu.db.tables.Users
import ru.yarsu.db.tables.manyToMany.SpotsDays
import ru.yarsu.db.tables.manyToMany.UsersDays
import ru.yarsu.db.tables.manyToMany.UsersSpots
import ru.yarsu.web.domain.article.Spot
import ru.yarsu.web.domain.classes.DayOccupation
import ru.yarsu.web.domain.classes.User

class DataBaseController {
    /**
     * Создаёт таблицы в базе данных.
     */
    fun init() {
        transaction {
            SchemaUtils.create(DayOccupations, HourOccupations, Spots, Users, SpotsDays, UsersDays, UsersSpots)
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
    fun getUserByPage(
        page: Int,
        limit: Int,
    ): List<User> = UsersController().getUsersByPage(page, limit)

    fun getUserById(id: Int): User = UsersController().getUserById(id)

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

    // Работа со Spots
    fun getSpotsByPage(
        page: Int,
        limit: Int,
    ): List<Spot> = SpotsController().getSpotsByPage(page, limit)

    fun getSpotById(id: Int): Spot = SpotsController().getSpotById(id)

    fun insertSpot(spot: Spot): Int = SpotsController().insertSpot(spot)

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
        dayId: Int,
        targetHour: Int,
        userId: Int,
    ): Boolean = OccupationsController().occupyHour(dayId, targetHour, userId)

    fun unoccupyHour(
        dayId: Int,
        targetHour: Int,
        userId: Int,
    ): Boolean = OccupationsController().unoccupyHour(dayId, targetHour, userId)
}
