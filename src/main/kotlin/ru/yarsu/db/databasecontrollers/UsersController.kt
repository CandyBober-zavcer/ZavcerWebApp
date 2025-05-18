package ru.yarsu.db.databasecontrollers

import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.DayOccupationLine
import ru.yarsu.db.tables.DayOccupations
import ru.yarsu.db.tables.SpotLine
import ru.yarsu.db.tables.Spots
import ru.yarsu.db.tables.UserLine
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.RoleEnums

class UsersController {
    /**
     * Собирает список объектов класса User по заданному номеру страницы.
     * @return список User`ов. При неудаче список будет пустым.
     */
    fun getUsersByPage(
        page: Int,
        limit: Int,
    ): List<User> {
        val result = mutableListOf<User>()

        transaction {
            val users =
                UserLine
                    .all()
                    .offset((limit * page).toLong())
                    .limit(limit)
                    .toList()
            for (user in users) {
                result.add(packUser(user))
            }
        }
        return result
    }

    /**
     * Собирает объект класса User из строчки базы данных по выбранному ID.
     * @return класс User (duh). При неудаче класс будет с ID, равным -1.
     */
    fun getUserById(id: Int): User {
        var user = User()

        transaction {
            val userLine = UserLine.findById(id)
            userLine?.let {
                user = packUser(it)
//                user.id = id
//                user.name = it.name
//                user.tg_name = it.tg_name
//                user.password = it.password
//                user.phone = it.phone
//                user.experience = it.experience
//                user.abilities = stringToAbilities(it.abilities)
//                user.price = it.price
//                user.description = it.description
//                user.address = it.address
//                user.district = DistrictEnums.from(it.district) ?: DistrictEnums.UNKNOWN
//                user.images = it.images.toMutableList()
//                user.twoWeekOccupation = it.twoWeekOccupation.map { day -> day.id.value }.toMutableList()
//                user.spots = it.spots.map { spot -> spot.id.value }.toMutableList()
//                user.roles = stringToRoles(it.roles)
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
                    phone = user.phone
                    experience = user.experience
                    abilities = abilitiesToString(user.abilities)
                    price = user.price
                    description = user.description
                    address = user.address
                    district = user.district.id
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
                it.phone = data.phone
                it.experience = data.experience
                it.abilities = abilitiesToString(data.abilities)
                it.price = data.price
                it.description = data.description
                it.address = data.address
                it.district = data.district.id
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

    /**
     * 10000 - GUITAR
     * 01000 - BASS
     * 00100 - DRUMS
     * 00010 - VOICE
     * 00001 - KEYBOARD
     */
    private fun abilitiesToString(abilities: Set<AbilityEnums>): String {
        val result = StringBuilder("00000")

        for (i in AbilityEnums.entries) {
            if (abilities.contains(i)) {
                result[i.id] = '1'
            }
        }
        return result.toString()
    }

    /**
     * 10000 - GUITAR
     * 01000 - BASS
     * 00100 - DRUMS
     * 00010 - VOICE
     * 00001 - KEYBOARD
     */
    private fun stringToAbilities(str: String): MutableSet<AbilityEnums> {
        val result = mutableSetOf<AbilityEnums>()

        for (i in str.indices) {
            if (str[i] == '1') {
                result.add(AbilityEnums.from(i) ?: AbilityEnums.VOICE)
            }
        }
        return result
    }

    private fun packUser(line: UserLine): User {
        val user = User()

        user.id = line.id.value
        user.name = line.name
        user.tg_name = line.tg_name
        user.password = line.password
        user.phone = line.phone
        user.experience = line.experience
        user.abilities = stringToAbilities(line.abilities)
        user.price = line.price
        user.description = line.description
        user.address = line.address
        user.district = DistrictEnums.from(line.district) ?: DistrictEnums.UNKNOWN
        user.images = line.images.toMutableList()
        user.twoWeekOccupation = line.twoWeekOccupation.map { day -> day.id.value }.toMutableList()
        user.spots = line.spots.map { spot -> spot.id.value }.toMutableList()
        user.roles = stringToRoles(line.roles)

        return user
    }
}
