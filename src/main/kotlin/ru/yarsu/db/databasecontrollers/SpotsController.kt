package ru.yarsu.db.databasecontrollers

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.*
import ru.yarsu.db.tables.manyToMany.*
import ru.yarsu.web.domain.article.Spot
import ru.yarsu.web.domain.enums.DistrictEnums
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull

class SpotsController {
    /**
     * Собирает список объектов класса Spot по заданному номеру страницы.
     * @param drums Boolean? Если не null, производит фильтрацию по наличию барабанов. По умолчанию null.Add commentMore actions
     * @param guitarAmps Содержит необходимый минимум гитарных комбиков. По умолчанию 0.
     * @param bassAmps Содержит необходимый минимум басовых комбиков. По умолчанию 0.
     * @param districtList Список айдишников районов. По умолчанию состоит из всех айдишников.
     * @param priceLow Нижняя граница цены. По умолчанию 0.
     * @param priceHigh Верхняя граница цены. По умолчанию Int.MAX_VALUE.
     * @param sortByNearest Если true, оставляет в списке места со свободными часами
     * и сортирует по ближайшим к сегодняшней дате.
     * @return список Spot`ов. При неудаче список будет пустым.
     */
    fun getSpotsByPage(
        page: Int,
        limit: Int,
        drums: Boolean? = null,
        guitarAmps: Int = 0,
        bassAmps: Int = 0,
        districtList: List<DistrictEnums> = DistrictEnums.entries,
        priceLow: Int = 0,
        priceHigh: Int = Int.MAX_VALUE,
        sortByNearest: Boolean = false
    ): List<Spot> =
    transaction {
        val districts = districtList.map { it.id }
        Spots.selectAll()

        var whereClause =
            (Spots.district inList districts) and
                    (Spots.guitarAmps greaterEq guitarAmps) and
                    (Spots.bassAmps greaterEq bassAmps) and
                    (Spots.price greaterEq priceLow) and
                    (Spots.price lessEq priceHigh)

        drums?.let { whereClause = whereClause and (Spots.hasDrums eq it) }

        if (sortByNearest) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            (Spots innerJoin SpotsDays innerJoin DayOccupations innerJoin HourOccupations)
                .select(
                    whereClause and
                            (HourOccupations.occupation.isNull()) and
                            (DayOccupations.day greaterEq today)
                )
                .orderBy(
                    DayOccupations.day to SortOrder.ASC,
                    HourOccupations.hour to SortOrder.ASC
                )
                .limit(limit)
                .offset((page * limit).toLong())
                .map { packSpot(SpotLine.wrapRow(it)) }
                .distinctBy { it.id }
        } else {
            SpotLine.find { whereClause }
                .limit(limit)
                .offset((page * limit).toLong())
                .map { packSpot(it) }
                .toList()
        }
    }

    /**
     * Собирает класс Spot из строчки базы данных по выбранному ID.
     * @return класс Spot (duh). При неудаче класс будет с ID, равным -1.
     */
    fun getSpotById(id: Int): Spot {
        var spot = Spot()

        transaction {
            val spotLine = SpotLine.findById(id)
            spotLine?.let {
                spot = packSpot(it)
//                spot.id = it.id.value
//                spot.name = it.name
//                spot.price = it.price
//                spot.hasDrums = it.hasDrums
//                spot.guitarAmps = it.guitarAmps
//                spot.bassAmps = it.bassAmps
//                spot.description = it.description
//                spot.address = it.address
//                spot.district = DistrictEnums.from(it.district) ?: DistrictEnums.UNKNOWN
//                spot.images = it.images.toList()
//                spot.twoWeekOccupation = it.twoWeekOccupation.map { day -> day.id.value }.toMutableList()
//                spot.owners = it.owners.map { user -> user.id.value }.toMutableList()
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

    private fun packSpot(line: SpotLine): Spot {
        val spot = Spot()

        spot.id = line.id.value
        spot.name = line.name
        spot.price = line.price
        spot.hasDrums = line.hasDrums
        spot.guitarAmps = line.guitarAmps
        spot.bassAmps = line.bassAmps
        spot.description = line.description
        spot.address = line.address
        spot.district = DistrictEnums.from(line.district) ?: DistrictEnums.UNKNOWN
        spot.images = line.images.toList()
        spot.twoWeekOccupation = line.twoWeekOccupation.map { day -> day.id.value }.toMutableList()
        spot.owners = line.owners.map { user -> user.id.value }.toMutableList()

        return spot
    }
}
