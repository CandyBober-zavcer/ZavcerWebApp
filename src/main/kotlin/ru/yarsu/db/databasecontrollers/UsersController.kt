package ru.yarsu.db.databasecontrollers

import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.*
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.RoleEnums
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import ru.yarsu.db.tables.manyToMany.UsersDays
import ru.yarsu.web.domain.models.email.hashPassword
import ru.yarsu.web.domain.models.email.verifyPassword
import ru.yarsu.web.domain.models.telegram.TelegramUser

class UsersController {
    /**
     * Собирает список объектов класса User по заданному номеру страницы.
     * @param abilityIds Список айдишников умений преподавателя. По умолчанию состоит из всех айдишников.
     * @param districtIds Список айдишников районов. По умолчанию состоит из всех айдишников.
     * @param priceMin Нижняя граница цены. По умолчанию 0.
     * @param priceMax Верхняя граница цены. По умолчанию Int.MAX_VALUE.
     * @param experienceMin Нижняя граница преподавательского опыта.
     * @param sortByNearest Если true, оставляет в списке учителей со свободными часами
     * и сортирует по ближайшим к сегодняшней дате.
     * @return список User`ов. При неудаче список будет пустым.
     */
    fun getTeachersByPage(
        page: Int,
        limit: Int,
        abilityIds: List<Int> = AbilityEnums.entries.map { it.id },
        districtIds: List<Int> = DistrictEnums.entries.map { it.id },
        priceMin: Int = 0,
        priceMax: Int = Int.MAX_VALUE,
        experienceMin: Int = 0,
        sortByNearest: Boolean = false
    ): Pair<List<User>, Int> =
        transaction {
            val size: Int

            val whereClause =
                (Users.id inSubQuery UsersAbilities.select(UsersAbilities.user).where { UsersAbilities.ability inList abilityIds }) and
                        (Users.district inList districtIds) and
                        (Users.experience greaterEq experienceMin) and
                        (Users.roles.substring(RoleEnums.TEACHER.id + 1, 1) eq "1") and
                        (Users.price greaterEq priceMin) and
                        (Users.price lessEq priceMax)
            if (sortByNearest) {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val selectedColumns = Users.columns + HourOccupations.columns + DayOccupations.columns
                val query = Users
                    .innerJoin(UsersDays, { Users.id }, { user })
                    .innerJoin(DayOccupations, { UsersDays.day }, { id })
                    .innerJoin(HourOccupations, { DayOccupations.id }, { day })
                    .select (
                        columns = selectedColumns
                    ).where {
                        HourOccupations.occupation.isNull() and whereClause and (DayOccupations.day greaterEq today)
                    }
                    .orderBy(
                        DayOccupations.day to SortOrder.ASC,
                        Users.price to SortOrder.ASC
                    )

                size = query.count().toInt()
                Pair(query
                    .map { packUser(UserLine.wrapRow(it)) }.distinctBy { it.id }, size)
            } else {
                val query =
                UserLine.find { whereClause }
                    .orderBy(Users.price to SortOrder.ASC)
                size = query.count().toInt()
                Pair(query
                    .map { packUser(it) }
                    .toList(), size)
            }
        }

    /**
     * Собирает объект класса User из строчки базы данных по выбранному ID.
     * @return класс User (duh). При неудаче будет null.
     */
    fun getUserById(id: Int): User? {
        var user: User? = null

        transaction {
            val userLine = UserLine.findById(id)
            userLine?.let {
                user = packUser(it)
            }
        }
        return user
    }

    fun getUserByEmail(email: String): User? {
        var user: User? = null

        transaction {
            val userLine = UserLine.find { Users.login eq email }.firstOrNull()
            userLine?.let {
                user = packUser(it)
            }
        }
        return user
    }

    fun getUserByLogin(login: String): User? {
        var user: User? = null

        transaction {
            val userLine = UserLine.find { Users.login eq login }.firstOrNull()
            userLine?.let {
                user = packUser(it)
            }
        }
        return user
    }

    fun getTeacherById(id: Int): User? {
        var user: User? = null

        transaction {
            val userLine = UserLine.findById(id)
            userLine?.let {
                val roles = stringToRoles(it.roles)
                if (roles.contains(RoleEnums.TEACHER)) {
                    user = packUser(it)
                }
            }
        }
        return user
    }

    fun getTeacherByIdIfRolePendingTeacher(id: Int): User? =
        transaction {
            UserLine.find {
                (Users.id eq id) and
                (Users.roles.substring(5, 1) eq "1" ) and
                (Users.isConfirmed eq true)
            }.firstOrNull()?.let { packUser(it) }
        }

    fun getUserIfNotTeacher(id: Int): User? =
        transaction {
            UserLine.find {
                (Users.id eq id) and
                        (not(Users.roles.substring(5, 1) eq "1" ))
            }.firstOrNull()?.let { packUser(it) }
        }

    fun getAllUsersByRole(role: Int): List<User>
        = transaction {
            UserLine.find { Users.roles.substring(role + 1, 1) eq "1" }.map { packUser(it) }.toList()
        }

    fun updateTeacherRequest(id: Int, accept: Boolean): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(id)
            user?.let {
                val newRoles = stringToRoles(it.roles)
                newRoles.remove(RoleEnums.PENDING_TEACHER)
                if (accept) {
                    newRoles.add(RoleEnums.TEACHER)
                }
                it.roles = rolesToString(newRoles)
                result = true
            }
        }
        return result
    }

    fun removeTeacherRoleById(id: Int): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(id)
            user?.let {
                val roles = stringToRoles(it.roles)
                if (roles.contains(RoleEnums.TEACHER)) {
                    roles.remove(RoleEnums.TEACHER)
                    user.roles = rolesToString(roles)
                    result = true
                }
            }
        }
        return result
    }

    fun confirmUser(userId: Int): Boolean {
        var result = false
        transaction {
            val user = UserLine.findById(userId)
            user?.let {
                it.isConfirmed = true
                result = true
            }
        }
        return result
    }

    fun updateUserPassword(
        userId: Int,
        newPassword: String,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let {
                it.password = hashPassword(newPassword)
                result = true
            }
        }
        return result
    }

    fun findOrCreateTelegramUser(telegramData: TelegramUser): User {
        val existing = getUserByTelegramId(telegramData.id)
        if (existing != null) return existing

        val name =
            listOfNotNull(telegramData.first_name, telegramData.last_name)
                .joinToString(" ")
                .ifBlank { telegramData.username ?: "TelegramUser" }

        val login = telegramData.username ?: "tg_user_${telegramData.id}"

        return getUserById(insertUser(
            User(
                name = name,
                login = login,
                tg_id = telegramData.id,
                password = "",
                experience = 0,
                abilities = mutableSetOf(),
                price = 0,
                description = "",
                address = "",
                district = DistrictEnums.UNKNOWN,
                images = mutableListOf(),
                roles = mutableSetOf(RoleEnums.USER),
                isConfirmed = true,
            ),
        )) ?: User()
    }

    fun attachTelegram(
        userId: Int,
        telegramId: Long,
    ): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(userId)
            user?.let {
                it.tg_id = telegramId
                result = true
            }
        }
        return result
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
            UserLine
                .new {
                    name = user.name
                    tg_id = user.tg_id
                    login = user.login
                    password = user.password
                    phone = user.phone
                    experience = user.experience
                    price = user.price
                    description = user.description
                    address = user.address
                    district = user.district.id
                    images = user.images.toTypedArray()
                    roles = rolesToString(user.roles)
                    isConfirmed = user.isConfirmed
                }.withAbilities(user.abilities.map { it.id })
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
    ): Boolean =
        transaction {
            UserLine.findById(id)?.let { user ->
                user.apply {
                    name = data.name
                    tg_id = data.tg_id
                    login = data.login
                    password = data.password
                    phone = data.phone
                    experience = data.experience
                    price = data.price
                    description = data.description
                    address = data.address
                    district = data.district.id
                    images = data.images.toTypedArray()
                    roles = rolesToString(data.roles)
                    isConfirmed = data.isConfirmed
                }
                user.updateAbilities(data.abilities.map { it.id })

                true
            } ?: false
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

    fun verifyPassword(
        user: User,
        password: String,
    ): Boolean = verifyPassword(password, user.password)

    /**
     * 1000 - ADMIN
     * 0100 - OWNER
     * 0010 - TEACHER
     * 0001 - USER
     * 0000 - ANONYMOUS
     */
    private fun rolesToString(roles: Set<RoleEnums>): String {
        val result = StringBuilder("00000000")

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

    fun packUser(line: UserLine): User {
        val user = User()

        user.id = line.id.value
        user.name = line.name
        user.tg_id = line.tg_id
        user.login = line.login
        user.password = line.password
        user.phone = line.phone
        user.experience = line.experience
        user.abilities = line.abilities.map { AbilityEnums.from(it.ability) ?: AbilityEnums.VOICE }.toMutableSet()
        user.price = line.price
        user.description = line.description
        user.address = line.address
        user.district = DistrictEnums.from(line.district) ?: DistrictEnums.UNKNOWN
        user.images = line.images.toMutableList()
        user.twoWeekOccupation = line.twoWeekOccupation.map { day -> day.id.value }.toMutableList()
        user.spots = line.spots.map { spot -> spot.id.value }.toMutableList()
        user.roles = stringToRoles(line.roles)
        user.isConfirmed = line.isConfirmed

        return user
    }

    private fun getUserByTelegramId(tgId: Long): User? {
        var user: User? = null

        transaction {
            val userLine = UserLine.find { Users.tg_id eq tgId }.firstOrNull()
            userLine?.let {
                user = packUser(it)
            }
        }
        return user
    }

    fun getOwnerByIdIfRolePendingOwner(id: Int): User? =
        transaction {
            UserLine.find {
                (Users.id eq id) and
                        (Users.roles.substring(6, 1) eq "1" ) and
                        (Users.isConfirmed eq true)
            }.firstOrNull()?.let { packUser(it) }
        }

    fun getUserIfNotOwner(id: Int): User? =
        transaction {
            UserLine.find {
                (Users.id eq id) and
                        not(Users.roles.substring(1, 1) eq "1") and
                        (Users.isConfirmed eq true)
            }.firstOrNull()?.let { packUser(it) }
        }

    fun updateUserRole(id: Int, role: RoleEnums, add: Boolean): Boolean = transaction {
        val user = UserLine.findById(id) ?: return@transaction false

        val roles = stringToRoles(user.roles).toMutableSet()
        if (add) roles.add(role) else roles.remove(role)

        user.roles = rolesToString(roles)
        true
    }

    fun acceptTeacherRequest(id: Int) = updateUserRole(id, RoleEnums.TEACHER, add = true)
    fun rejectTeacherRequest(id: Int) = updateUserRole(id, RoleEnums.TEACHER, add = false)
    fun acceptOwnerRequest(id: Int) = updateUserRole(id, RoleEnums.OWNER, add = true)
    fun rejectOwnerRequest(id: Int) = updateUserRole(id, RoleEnums.OWNER, add = false)

    fun getPendingTeachers(): List<User> = transaction {
        UserLine.find {
            (Users.roles.substring(5, 1) eq "1") and
                    (Users.isConfirmed eq true)
        }.map { packUser(it) }
    }

    fun getPendingOwners(): List<User> = transaction {
        UserLine.find {
            (Users.roles.substring(1, 1) eq "1") and
                    (Users.isConfirmed eq true)
        }.map { packUser(it) }
    }

    fun addTeacherRoleById(id: Int): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(id)
            user?.let {
                val roles = stringToRoles(it.roles)
                if (!roles.contains(RoleEnums.TEACHER)) {
                    roles.add(RoleEnums.TEACHER)
                    user.roles = rolesToString(roles)
                    result = true
                }
            }
        }
        return result
    }

    fun addOwnerRoleById(id: Int): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(id)
            user?.let {
                val roles = stringToRoles(it.roles)
                if (!roles.contains(RoleEnums.OWNER)) {
                    roles.add(RoleEnums.OWNER)
                    user.roles = rolesToString(roles)
                    result = true
                }
            }
        }
        return result
    }

    fun removeOwnerRoleById(id: Int): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(id)
            user?.let {
                val roles = stringToRoles(it.roles)
                if (roles.contains(RoleEnums.OWNER)) {
                    roles.remove(RoleEnums.OWNER)
                    user.roles = rolesToString(roles)
                    result = true
                }
            }
        }
        return result
    }

    fun deleteUser(id: Int): Boolean {
        var result = false

        transaction {
            val user = UserLine.findById(id)
            if (user != null) {
                Users.deleteWhere { Users.id eq id }

                user.delete()

                result = true
            }
        }

        return result
    }

    fun getOwnerById(id: Int): User? = transaction {
        val user = UserLine.findById(id) ?: return@transaction null
        val roles = stringToRoles(user.roles)
        if (RoleEnums.OWNER in roles && user.isConfirmed) {
            packUser(user)
        } else {
            null
        }
    }

    fun getAllUsers(): List<User> = transaction {
        UserLine.all().map { packUser(it) }.toList()
    }

}
