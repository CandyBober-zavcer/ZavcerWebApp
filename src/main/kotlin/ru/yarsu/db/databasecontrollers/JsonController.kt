package ru.yarsu.db.databasecontrollers

import kotlinx.serialization.Serializable
import ru.yarsu.db.tables.SpotLine
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import ru.yarsu.db.tables.UserLine

class JsonController {
    companion object {
        //Свободные часы у точки
        fun getAvailableDatesForSpotJson(spotId: Int): String = transaction {
            val spot = SpotLine.findById(spotId) ?: return@transaction ""
            val availableDates = mutableMapOf<String, List<String>>()

            spot.twoWeekOccupation.forEach { dayOccupation ->
                val dateStr = dayOccupation.day.toString()
                val availableHours = dayOccupation.hours
                    .filter { hour -> hour.occupation == null }
                    .map { "${it.hour}:00" }
                    .sorted()

                if (availableHours.isNotEmpty()) {
                    availableDates[dateStr] = availableHours
                }
            }

            Json.encodeToString(availableDates)
        }


        //Свободные часы у учителя
        fun getAvailableDatesForTeacherJson(userId: Int): String {
            return transaction {
                val user = UserLine.findById(userId) ?: return@transaction ""
                val availableDates = mutableMapOf<String, List<String>>()

                user.twoWeekOccupation.forEach { dayOccupation ->
                    val dateStr = dayOccupation.day.toString()
                    val availableHours = dayOccupation.hours
                        .filter { hour -> hour.occupation == null }
                        .map { "${it.hour}:00" }
                        .sorted()

                    if (availableHours.isNotEmpty()) {
                        availableDates[dateStr] = availableHours
                    }
                }

                Json.encodeToString(availableDates)
            }
        }



        // Занятые часы в точках юзера
        private fun getBlockedDatesForUserSpotsJson(userId: Int): String {
            val user = UserLine.findById(userId) ?: return ""
            val blockedData = mutableMapOf<String, MutableMap<String, MutableList<BlockedTimeSlot>>>()

            user.spots.forEach { spot ->
                val spotName = spot.name

                spot.twoWeekOccupation.forEach { dayOccupation ->
                    val dateStr = dayOccupation.day.toString()

                    val slots = dayOccupation.hours
                        .filter { it.occupation != null }
                        .map { hour ->
                            BlockedTimeSlot(
                                time = "${hour.hour}:00",
                                user = hour.occupation?.login ?: "Can anyone hear me?"
                            )
                        }

                    if (slots.isNotEmpty()) {
                        blockedData.getOrPut(spotName) { mutableMapOf() }[dateStr] = slots.toMutableList()
                    }
                }
            }

            return Json.encodeToString(blockedData)
        }

        //Свободные часы в точках юзера
        private fun getFreeDatesForUserSpotsJson(userId: Int): String {
            val user = UserLine.findById(userId) ?: return ""
            val freeDates = mutableMapOf<String, MutableMap<String, MutableList<String>>>()

            user.spots.forEach { spot ->
                val spotName = spot.name

                spot.twoWeekOccupation.forEach { dayOccupation ->
                    val dateStr = dayOccupation.day.toString()

                    val freeSlots = dayOccupation.hours
                        .filter { it.occupation == null }
                        .map { "${it.hour}:00" }
                        .sorted()

                    if (freeSlots.isNotEmpty()) {
                        freeDates.getOrPut(spotName) { mutableMapOf() }[dateStr] = freeSlots.toMutableList()
                    }
                }
            }

            return Json.encodeToString(freeDates)
        }

        //Занятые часы у учителя
        fun getBlockedDatesForTeacherJson(userId: Int): String {
            val user = UserLine.findById(userId) ?: return ""

            val blockedDates = mutableMapOf<String, MutableList<BlockedTimeSlot>>()
            user.twoWeekOccupation.forEach { dayOccupation ->
                val dateStr = dayOccupation.day.toString()

                val slots = dayOccupation.hours
                    .filter { it.occupation != null }
                    .map { hour ->
                        BlockedTimeSlot(
                            time = "${hour.hour}:00",
                            user = hour.occupation?.login ?: "It's so dark here..."
                        )
                    }
                    .sortedBy { it.time }

                if (slots.isNotEmpty()) {
                    blockedDates[dateStr] = slots.toMutableList()
                }
            }

            return Json.encodeToString(blockedDates)
        }

        fun getFreeDatesForTeacherJson(userId: Int): String {
            val user = UserLine.findById(userId) ?: return ""
            val freeDates = mutableMapOf<String, MutableList<String>>()

            user.twoWeekOccupation.forEach { dayOccupation ->
                val dateStr = dayOccupation.day.toString()

                val freeSlots = dayOccupation.hours
                    .filter { it.occupation == null }
                    .map { "${it.hour}:00" }
                    .sorted()

                if (freeSlots.isNotEmpty()) {
                    freeDates[dateStr] = freeSlots.toMutableList()
                }
            }

            return Json.encodeToString(freeDates)
        }
    }
}

@Serializable
data class BlockedTimeSlot(
    val time: String,
    val user: String
)