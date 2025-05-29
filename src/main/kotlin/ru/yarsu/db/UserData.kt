package ru.yarsu.db

import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.enums.*
import ru.yarsu.web.domain.models.email.hashPassword
import ru.yarsu.web.domain.models.email.verifyPassword
import ru.yarsu.web.domain.models.telegram.TelegramUser

class UserData {
    private val users = mutableListOf<UserModel>()
    private var nextId = 1

    init {
        fillData()
    }

    private fun fillData() {
        add(
            UserModel(
                name = "Иван Иванов",
                tg_id = 1234567890L,
                login = "ivan_music",
                password = "123456",
                experience = 5,
                abilities = setOf(AbilityEnums.GUITAR, AbilityEnums.VOICE),
                price = 1000,
                description = "Гитарист и вокалист с опытом",
                address = "ул. Музыкальная, 12",
                district = DistrictEnums.KIROVSKY,
                images = listOf("profile1.jpg"),
                roles = setOf(RoleEnums.USER),
            ),
        )
        add(
            UserModel(
                name = "Мария Смирнова",
                tg_id = 9876543210L,
                login = "maria_drummer",
                password = "pass321",
                experience = 2,
                abilities = setOf(AbilityEnums.DRUMS),
                price = 800,
                description = "Барабанщица с энергией!",
                address = "пр. Победы, 3",
                district = DistrictEnums.LENINSKY,
                images = listOf("profile2.jpg"),
                roles = setOf(RoleEnums.USER, RoleEnums.TEACHER),
            ),
        )
    }

    fun add(user: UserModel): UserModel {
        val hashedUser =
            user.copy(
                id = nextId++,
                password = hashPassword(user.password),
            )
        users.add(hashedUser)
        return hashedUser
    }

    fun update(
        id: Int,
        updatedUser: UserModel,
    ): Boolean {
        val index = users.indexOfFirst { it.id == id }
        return if (index != -1) {
            users[index] = updatedUser.copy(id = id)
            true
        } else {
            false
        }
    }

    fun deleteById(id: Int): Boolean = users.removeIf { it.id == id }

    fun getById(id: Int): UserModel? = users.find { it.id == id }

    fun getAll(): List<UserModel> = users.filter { it.isConfirmed }

    fun getTeachers(): List<UserModel> =
        users.filter {
            RoleEnums.TEACHER in it.roles && it.isConfirmed
        }

    fun getTeacherById(id: Int): UserModel? =
        users.find {
            it.id == id && RoleEnums.TEACHER in it.roles && it.isConfirmed
        }

    fun removeTeacherRoleById(id: Int): Boolean {
        val user = users.find { it.id == id }
        return if (user != null && RoleEnums.TEACHER in user.roles) {
            val updatedRoles = user.roles.toMutableSet().apply { remove(RoleEnums.TEACHER) }
            val updatedUser = user.copy(roles = updatedRoles)
            update(id, updatedUser)
        } else {
            false
        }
    }

    fun existsByTelegramId(tgId: Long): Boolean = users.any { it.tg_id == tgId }

    fun findByTelegramId(tgId: Long): UserModel? = users.find { it.tg_id == tgId }

    fun findByLogin(login: String): UserModel? = users.find { it.login == login }

    fun existsByLogin(login: String): Boolean = users.any { it.login == login }

    fun confirmUser(userId: Int): Boolean {
        val index = users.indexOfFirst { it.id == userId }
        if (index != -1) {
            val user = users[index]
            users[index] = user.copy(isConfirmed = true)
            return true
        }
        return false
    }

    fun getByEmail(email: String): UserModel? = users.find { it.login.equals(email, ignoreCase = true) }

    fun verifyPassword(
        user: UserModel,
        password: String,
    ): Boolean = verifyPassword(password, user.password)

    fun getOrCreateTelegramUser(telegramUser: TelegramUser): UserModel {
        val existing = getUserByTelegramId(telegramUser.id)
        if (existing != null) return existing

        val name =
            listOfNotNull(telegramUser.first_name, telegramUser.last_name)
                .joinToString(" ")
                .ifBlank { telegramUser.username ?: "TelegramUser" }

        return createUserFromTelegram(telegramUser.id, name)
    }

    fun getUserByTelegramId(id: Long): UserModel? = users.find { it.tg_id == id }

    fun createUserFromTelegram(
        telegramId: Long,
        name: String,
    ): UserModel =
        add(
            UserModel(
                name = name,
                tg_id = telegramId,
                password = "",
                roles = setOf(RoleEnums.USER),
            ),
        )
}
