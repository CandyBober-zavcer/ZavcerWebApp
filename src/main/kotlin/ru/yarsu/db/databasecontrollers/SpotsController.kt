package ru.yarsu.db.databasecontrollers

import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.DayOccupationLine
import ru.yarsu.db.tables.DayOccupations
import ru.yarsu.db.tables.SpotLine
import ru.yarsu.db.tables.UserLine
import ru.yarsu.db.tables.Users
import ru.yarsu.web.domain.classes.Spot
import ru.yarsu.web.domain.enums.DistrictEnums

class SpotsController {
    /**
     * Собирает класс Spot из строчки базы данных по выбранному ID.
     * @return класс Spot (duh). При неудаче класс будет с ID, равным -1.
     */
    fun getSpotById(id: Int): Spot {
        val spot = Spot()

        transaction {
            val spotLine = SpotLine.findById(id)
            spotLine?.let {
                spot.id = it.id.value
                spot.name = it.name
                spot.price = it.price
                spot.hasDrums = it.hasDrums
                spot.guitarAmps = it.guitarAmps
                spot.bassAmps = it.bassAmps
                spot.description = it.description
                spot.address = it.address
                spot.district = DistrictEnums.from(it.district) ?: DistrictEnums.UNKNOWN
                spot.images = it.images.toList()
                spot.twoWeekOccupation = it.twoWeekOccupation.map { day -> day.id.value }.toMutableList()
                spot.owners = it.owners.map { user -> user.id.value }.toMutableList()
            }
        }
        return spot
    }

    /**
     * Добавляет в базу данных строчку о новом месте.
     * Создаётся без владельцев и расписания свободных дней.
     * @return ID новой строки, -1 - иначе
     */
    fun insertSpot(spot: Spot): Int {
        var id = -1

        transaction {
            val spotLine =
                SpotLine.new {
                    name = spot.name
                    price = spot.price
                    hasDrums = spot.hasDrums
                    guitarAmps = spot.guitarAmps
                    bassAmps = spot.bassAmps
                    description = spot.description
                    address = spot.address
                    district = spot.district.id
                    images = spot.images.toTypedArray()
                }
            id = spotLine.id.value
        }
        return id
    }

    /**
     * Обновляет имя, описание и приложенные фотографии у места.
     * Не затрагивает расписание и владельцев.
     * @return true при удачной операции, false при неудаче.
     */
    fun updateSpotInfo(
        id: Int,
        data: Spot,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(id)
            spot?.let {
                it.name = data.name
                it.price = data.price
                it.hasDrums = data.hasDrums
                it.guitarAmps = data.guitarAmps
                it.bassAmps = data.bassAmps
                it.description = data.description
                it.address = data.address
                it.district = data.district.id
                it.images = data.images.toTypedArray()
                result = true
            }
        }
        return result
    }

    /**
     * Добавляет пользователя в список владельцев места.
     * @return true при удачной операции, false при неудаче.
     */
    fun addOwner(
        spotId: Int,
        userId: Int,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val user = UserLine.findById(userId)
                user?.let { usit ->
                    spit.owners = SizedCollection(spit.owners + usit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Добавляет нескольких пользователей в список владельцев места.
     * @return true при удачной операции, false при неудаче.
     */
    fun addOwner(
        spotId: Int,
        userIds: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val users = UserLine.find { Users.id inList userIds }.toList()
                if (users.isNotEmpty()) {
                    spit.owners = SizedCollection(spit.owners + users)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет пользователя из списка владельцев места.
     * @return true при удачной операции, false при неудаче.
     */
    fun removeOwner(
        spotId: Int,
        userId: Int,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val user = UserLine.findById(userId)
                user?.let { usit ->
                    spit.owners = SizedCollection(spit.owners - usit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет нескольких пользователей из списка владельцев места.
     * @return true при удачной операции, false при неудаче.
     */
    fun removeOwner(
        spotId: Int,
        userIds: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val users = UserLine.find { Users.id inList userIds }.toList()
                if (users.isNotEmpty()) {
                    spit.owners = SizedCollection(spit.owners - users.toSet())
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Добавляет один день расписания к месту.
     * @return true при удачной операции, false при неудаче.
     */
    fun addDayOccupation(
        spotId: Int,
        dayId: Int,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val day = DayOccupationLine.findById(dayId)
                day?.let { dayit ->
                    spit.twoWeekOccupation = SizedCollection(spit.twoWeekOccupation + dayit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Добавляет несколько дней расписания к месту.
     * @return true при удачной операции, false при неудаче.
     */
    fun addDayOccupation(
        spotId: Int,
        daysId: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val days = DayOccupationLine.find { DayOccupations.id inList daysId }.toList()
                if (days.isNotEmpty()) {
                    spit.twoWeekOccupation = SizedCollection(spit.twoWeekOccupation + days)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет один день из расписания к месту (нахуя?).
     * @return true при удачной операции, false при неудаче.
     */
    fun removeDayOccupation(
        spotId: Int,
        dayId: Int,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val day = DayOccupationLine.findById(dayId)
                day?.let { dayit ->
                    spit.twoWeekOccupation = SizedCollection(spit.twoWeekOccupation - dayit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет несколько дней из расписания к месту (нахуя?).
     * @return true при удачной операции, false при неудаче.
     */
    fun removeDayOccupation(
        spotId: Int,
        daysId: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val spot = SpotLine.findById(spotId)
            spot?.let { spit ->
                val days = DayOccupationLine.find { DayOccupations.id inList daysId }.toList()
                if (days.isNotEmpty()) {
                    spit.twoWeekOccupation = SizedCollection(spit.twoWeekOccupation - days.toSet())
                    result = true
                }
            }
        }
        return result
    }
}
