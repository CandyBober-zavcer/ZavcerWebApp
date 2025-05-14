package ru.yarsu.db.databasecontrollers

import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.DayOccupationLine
import ru.yarsu.db.tables.DayOccupations
import ru.yarsu.db.tables.SpotLine
import ru.yarsu.db.tables.Spots
import ru.yarsu.db.tables.UserLine
import ru.yarsu.web.domain.RoleEnums
import ru.yarsu.web.domain.classes.User

class UsersController {
    /**
     * Собирает класс User из строчки базы данных по выбранному ID.
     * @return класс User (duh). При неудаче класс будет с ID, равным -1.
     */
    fun getUserById(id: Int): User {
        val user = User()

        transaction {
            val userLine = UserLine.findById(id)
            userLine?.let {
                user.id = id
                user.name = it.name
                user.tg_name = it.tg_name
                user.password = it.password
                user.description = it.description
                user.images = it.images?.toMutableList() ?: mutableListOf()
                user.twoWeekOccupation = it.twoWeekOccupation.map { day -> day.id.value }.toMutableList()
                user.spots = it.spots.map { spot -> spot.id.value }.toMutableList()
                user.roles = stringToRoles(it.roles)
            }
        }
        return user
    }

    /**
     * Добавляет в базу данных строчку о новом пользователе.
     * Создаётся без точек во владении и расписания свободных дней.
     * @return ID новой строки, -1 - иначе
     */
    fun insertUser(user: User): Int {
        var id = -1

        transaction {
            val userLine =
                UserLine.new {
                    name = user.name
                    tg_name = user.tg_name
                    password = user.password
                    description = user.description
                    images = user.images.toTypedArray()
                    roles = rolesToString(user.roles)
                }
            id = userLine.id.value
        }
        return id
    }

    /**
     * Обновляет имя, telegram, пароль, описание,
     * приложенные фотографии и роль пользователя.
     * Не затрагивает расписание и точки во владении.
     * @return true при удачной операции, false при неудаче.
     */
    fun updateUserInfo(
        id: Int,
        data: User,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(id)
            user?.let {
                it.name = data.name
                it.tg_name = data.tg_name
                it.password = data.password
                it.description = data.description
                it.images = data.images.toTypedArray()
                it.roles = rolesToString(data.roles)
                result = true
            }
        }
        return result
    }

    /**
     * Добавляет место в список владений пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun addSpot(
        userId: Int,
        spotId: Int,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val spot = SpotLine.findById(spotId)
                spot?.let { spit ->
                    usit.spots = SizedCollection(usit.spots + spit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Добавляет несколько точек в список владений пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun addSpot(
        userId: Int,
        spotIds: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val spots = SpotLine.find { Spots.id inList spotIds }.toList()
                if (spots.isNotEmpty()) {
                    usit.spots = SizedCollection(usit.spots + spots)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет место из списка владений пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun removeSpot(
        userId: Int,
        spotId: Int,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val spot = SpotLine.findById(spotId)
                spot?.let { spit ->
                    usit.spots = SizedCollection(usit.spots - spit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет несколько точек из списка владений пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun removeSpot(
        userId: Int,
        spotIds: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val spots = SpotLine.find { Spots.id inList spotIds }.toList()
                if (spots.isNotEmpty()) {
                    usit.spots = SizedCollection(usit.spots - spots.toSet())
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Добавляет один день к расписанию пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun addDayOccupation(
        userId: Int,
        dayId: Int,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val day = DayOccupationLine.findById(dayId)
                day?.let { dayit ->
                    usit.twoWeekOccupation = SizedCollection(usit.twoWeekOccupation + dayit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Добавляет несколько дней к расписанию пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun addDayOccupation(
        userId: Int,
        daysId: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val days = DayOccupationLine.find { DayOccupations.id inList daysId }.toList()
                if (days.isNotEmpty()) {
                    usit.twoWeekOccupation = SizedCollection(usit.twoWeekOccupation + days)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет один день у расписания пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun removeDayOccupation(
        userId: Int,
        dayId: Int,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val day = DayOccupationLine.findById(dayId)
                day?.let { dayit ->
                    usit.twoWeekOccupation = SizedCollection(usit.twoWeekOccupation - dayit)
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Удаляет несколько дней у расписания пользователя.
     * @return true при удачной операции, false при неудаче.
     */
    fun removeDayOccupation(
        userId: Int,
        daysId: List<Int>,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let { usit ->
                val days = DayOccupationLine.find { DayOccupations.id inList daysId }.toList()
                if (days.isNotEmpty()) {
                    usit.twoWeekOccupation = SizedCollection(usit.twoWeekOccupation - days.toSet())
                    result = true
                }
            }
        }
        return result
    }

    /**
     * 1000 - ADMIN
     * 0100 - OWNER
     * 0010 - TEACHER
     * 0001 - USER
     * 0000 - ANONYMOUS
     */
    private fun rolesToString(roles: Set<RoleEnums>): String {
        val result = StringBuilder("0000")

        for (i in RoleEnums.entries) {
            if (i == RoleEnums.ANONYMOUS) {
                continue
            }
            if (roles.contains(i)) {
                result[i.id] = '1'
            }
        }
        return result.toString()
    }

    /**
     * 1000 - ADMIN
     * 0100 - OWNER
     * 0010 - TEACHER
     * 0001 - USER
     * 0000 - ANONYMOUS
     */
    private fun stringToRoles(str: String): MutableSet<RoleEnums> {
        val result = mutableSetOf<RoleEnums>()
        var anon = true

        for (i in str.indices) {
            if (str[i] == '1') {
                anon = false
                result.add(RoleEnums.from(i) ?: RoleEnums.ANONYMOUS)
            }
        }
        return if (anon) {
            mutableSetOf(RoleEnums.ANONYMOUS)
        } else {
            result
        }
    }
}
