package ru.yarsu.db

import ru.yarsu.web.domain.article.Spot
import ru.yarsu.web.domain.enums.DistrictEnums
import java.nio.file.Files
import java.nio.file.Paths

class SpotData {
    private val spots = mutableListOf<Spot>()
    private var nextId = 1

    init {
        fillData()
    }

    private fun fillData() {
        add(
            Spot(
                name = "Гаражная студия",
                price = 500,
                hasDrums = true,
                guitarAmps = 2,
                bassAmps = 1,
                description = "Небольшая уютная студия с отличной акустикой.",
                address = "Ярославль, ул. Музыкальная, 1",
                district = DistrictEnums.KIROVSKY,
                images = listOf("studio1.webp", "studio2.webp"),
                owners = listOf(1),
            ),
        )
        add(
            Spot(
                name = "PRO звук",
                price = 1000,
                hasDrums = true,
                guitarAmps = 3,
                bassAmps = 2,
                description = "Профессиональная студия для репетиций и записи.",
                address = "Ярославль, пр. Ленина, 45",
                district = DistrictEnums.LENINSKY,
                images = listOf("studio3.jpg"),
                owners = listOf(2),
            ),
        )
    }

    fun add(spot: Spot): Spot {
        val newSpot = spot.copy(id = nextId++)
        spots.add(newSpot)
        return newSpot
    }

    fun update(
        id: Int,
        updatedSpot: Spot,
    ): Boolean {
        val index = spots.indexOfFirst { it.id == id }
        return if (index != -1) {
            spots[index] = updatedSpot.copy(id = id)
            true
        } else {
            false
        }
    }

    fun deleteById(id: Int): Boolean {
        val spot = spots.find { it.id == id } ?: return false
        deleteImages(spot.images)
        return spots.remove(spot)
    }

    fun getById(id: Int): Spot? = spots.find { it.id == id }

    fun getAll(): List<Spot> = spots.toList()

    fun getNextId(): Int = nextId

    private fun deleteImages(images: List<String>) {
        val basePath = Paths.get("public/img")
        images
            .filterNot { it == "defaultStudio.jpg" }
            .forEach { image ->
                val path = basePath.resolve(image)
                if (Files.exists(path)) {
                    try {
                        Files.delete(path)
                    } catch (e: Exception) {
                        println("Ошибка при удалении файла: $path — ${e.message}")
                    }
                }
            }
    }
}