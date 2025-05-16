package ru.yarsu.db.databasecontrollers

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.DayOccupationLine
import ru.yarsu.db.tables.DayOccupations
import ru.yarsu.db.tables.HourOccupationLine
import ru.yarsu.db.tables.UserLine
import ru.yarsu.web.domain.classes.DayOccupation

class OccupationsController {
    /**
     * Собирает класс DayOccupation из строчки базы данных по выбранному ID.
     * @return класс DayOccupation (duh). При неудаче класс будет с ID, равным -1.
     */
    fun getDayOccupationById(id: Int): DayOccupation {
        val dayOccupation = DayOccupation()

        transaction {
            addLogger(StdOutSqlLogger)
            val day = DayOccupationLine.findById(id)
            day?.let { line ->
                dayOccupation.id = id
                dayOccupation.occupation = line.hours.associateBy({ it.id.value }, { it.occupation?.id?.value ?: -1 }).toMutableMap()
            }
        }
        return dayOccupation
    }

    /**
     * Пользователь задаёт день для записи.
     * @return ID новой строки, -1 при неудаче.
     */
    fun insertDayOccupation(date: LocalDate): Int {
        var id = -1

        transaction {
            addLogger(StdOutSqlLogger)
            val day =
                DayOccupationLine.new {
                    day = date
                }
            id = day.id.value
        }
        return id
    }

    /**
     * Вставка одного свободного часа, поиск дня по ID.
     * @return ID новой строки (нахуя?), -1 при неудаче.
     */
    fun insertHourOccupation(
        time: Int,
        dateId: Int,
    ): Int {
        var id = -1

        transaction {
            addLogger(StdOutSqlLogger)
            val date = DayOccupationLine.findById(dateId)
            date?.let {
                val hourLine =
                    HourOccupationLine.new {
                        hour = time
                        occupation = null
                        day = it
                    }
                id = hourLine.id.value
            }
        }
        return id
    }

    /**
     * Вставка списка свободных часов, поиск дня по ID.
     * @return список ID новых строк (нахуя?), пустой список при неудаче.
     */
    fun insertHourGroupOccupation(
        times: List<Int>,
        dateId: Int,
    ): List<Int> {
        val ids = mutableListOf<Int>()

        transaction {
            addLogger(StdOutSqlLogger)
            val date = DayOccupationLine.findById(dateId)
            date?.let {
                for (time in times) {
                    val hourLine =
                        HourOccupationLine.new {
                            hour = time
                            occupation = null
                            day = it
                        }
                    ids.add(hourLine.id.value)
                }
            }
        }
        return ids
    }

    /**
     * Вставка одного свободного часа, поиск дня по дате.
     * @return ID новой строки (нахуя?), -1 при неудаче.
     */
    fun insertHourOccupation(
        time: Int,
        dateTarget: LocalDate,
    ): Int {
        var id = -1

        transaction {
            addLogger(StdOutSqlLogger)
            val date = DayOccupationLine.find { DayOccupations.day eq dateTarget }.singleOrNull()
            date?.let {
                val hourLine =
                    HourOccupationLine.new {
                        hour = time
                        occupation = null
                        day = it
                    }
                id = hourLine.id.value
            }
        }
        return id
    }

    /**
     * Вставка списка свободных часов, поиск дня по дате.
     * @return список ID новых строк (нахуя?), пустой список при неудаче.
     */
    fun insertHourGroupOccupation(
        times: List<Int>,
        dateTarget: LocalDate,
    ): List<Int> {
        val ids = mutableListOf<Int>()

        transaction {
            addLogger(StdOutSqlLogger)
            val date = DayOccupationLine.find { DayOccupations.day eq dateTarget }.singleOrNull()
            date?.let {
                for (time in times) {
                    val hourLine =
                        HourOccupationLine.new {
                            hour = time
                            occupation = null
                            day = it
                        }
                    ids.add(hourLine.id.value)
                }
            }
        }
        return ids
    }

    /**
     * Занимает выбранный час выбранным пользователем.
     * @return true при удачной вставке, false - иначе.
     */
    fun occupyHour(
        dayId: Int,
        targetHour: Int,
        userId: Int,
    ): Boolean {
        var inputRes = false

        transaction {
            addLogger(StdOutSqlLogger)
            val day = DayOccupationLine.findById(dayId)
            day?.let {
                val hour = day.hours.find { it.hour == targetHour }
//                toList().find { it.hour == targetHour }
                hour?.let { hoit ->
                    val user = UserLine.findById(userId)
                    user?.let { usit ->
                        hoit.occupation = usit
                        inputRes = true
                    }
                }
            }
        }
        return inputRes
    }

    /**
     * Освобождает выбранный час.
     * @return true при удачной операции, false - иначе.
     */
    fun unoccupyHour(
        dayId: Int,
        targetHour: Int,
        userId: Int,
    ): Boolean {
        var inputRes = false

        transaction {
            addLogger(StdOutSqlLogger)
            val day = DayOccupationLine.findById(dayId)
            day?.let {
                val hour = day.hours.find { it.hour == targetHour }
//                toList().find { it.hour == targetHour }
                hour?.let { hoit ->
                    hoit.occupation = null
                    inputRes = true
                }
            }
        }
        return inputRes
    }
}
