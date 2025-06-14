package ru.yarsu.db

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.yarsu.web.domain.classes.Spot
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.classes.DayOccupation
import ru.yarsu.web.domain.enums.AbilityEnums
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.RoleEnums
import ru.yarsu.web.domain.models.telegram.TelegramUser
import kotlin.test.*


@Testcontainers
class DatabaseControllerTest {
    private fun createTestUser(
        name: String = "Иван Иванов",
        tg_id: Long = 1234567890L,
        login: String = "ivan_music",
        password: String = "123456",
        phone: String = "+7 (4852) 73-88-15",
        experience: Int = 5,
        abilities: MutableSet<AbilityEnums> = mutableSetOf(AbilityEnums.GUITAR, AbilityEnums.VOICE),
        price: Int = 1000,
        description: String = "Гитарист и вокалист с опытом",
        address: String = "Ярославль, ул. Загородный Сад, 6",
        district: DistrictEnums = DistrictEnums.KIROVSKY,
        images: List<String> = listOf("profile1.jpg"),
        twoWeekOccupation: List<Int> = listOf(),
        spots: List<Int> = listOf(),
        roles: MutableSet<RoleEnums> = mutableSetOf(RoleEnums.USER),
        isConfirmed: Boolean = true
    ): User {
        return User(
            name = name,
            tg_id = tg_id,
            login = login,
            password = password,
            phone = phone,
            experience = experience,
            abilities = abilities,
            price = price,
            description = description,
            address = address,
            district = district,
            images = images,
            twoWeekOccupation = twoWeekOccupation,
            spots = spots,
            roles = roles,
            isConfirmed = isConfirmed
        )
    }

    private fun createMultipleTestUsers(count: Int): List<User> {
        return (1..count).map { i ->
            createTestUser(
                name = "User$i",
                login = "user$i",
                tg_id = 1234567890L + i,
                password = "pass$i",
                phone = "+7 (999) ${100 + i}-${1000 + i}",
                experience = i % 20,
                abilities = mutableSetOf(AbilityEnums.entries[i % AbilityEnums.entries.size]),
                price = 500 + (i * 100),
                description = "Description for User$i",
                address = "Address $i, Yaroslavl",
                district = DistrictEnums.entries[i % DistrictEnums.entries.size],
                images = listOf("profile$i.jpg")
            )
        }
    }

    private fun createTestSpot(
        name: String = "Гаражная студия",
        price: Int = 500,
        hasDrums: Boolean = true,
        guitarAmps: Int = 2,
        bassAmps: Int = 1,
        description: String = "Небольшая уютная студия с отличной акустикой.",
        address: String = "Ярославль, ул. Музыкальная, 1",
        district: DistrictEnums = DistrictEnums.KIROVSKY,
        images: List<String> = listOf("studio1.webp", "studio2.webp"),
        twoWeekOccupation: MutableList<Int> = mutableListOf(),
        owners: List<Int> = listOf(1)
    ): Spot {
        return Spot(
            name = name,
            price = price,
            hasDrums = hasDrums,
            guitarAmps = guitarAmps,
            bassAmps = bassAmps,
            description = description,
            address = address,
            district = district,
            images = images,
            twoWeekOccupation = twoWeekOccupation,
            owners = owners
        )
    }

    private fun createMultipleTestSpots(count: Int): List<Spot> {
        return (1..count).map { i ->
            createTestSpot(
                name = "Spot $i",
                description = "Description for spot $i",
                hasDrums = i % 2 == 0,
                guitarAmps = i % 3,
                bassAmps = i % 2,
                district = DistrictEnums.entries[i % DistrictEnums.entries.size],
                price = 300 + (i * 50),
                address = "Address $i, Yaroslavl",
                images = listOf("spot$i.webp"),
                owners = listOf(i)
            )
        }
    }

    private lateinit var dbController: DatabaseController

    companion object {
        @Container
        private val mysqlContainer = MySQLContainer("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")

        @JvmStatic
        @BeforeAll
        fun setUp() {
            DatabaseFactory.initForTests(
                mysqlContainer.jdbcUrl,
                mysqlContainer.username,
                mysqlContainer.password
            )
        }
    }

    @BeforeEach
    fun setUpEach() {
        dbController = DatabaseController()
        dbController.init()
    }

    @AfterEach
    fun tearDown() {
        transaction {
            exec("SET FOREIGN_KEY_CHECKS = 0")
            listOf("Users", "Spots", "HourOccupations", "DayOccupations", "SpotsDays", "UsersDays", "UsersSpots").forEach { table ->
                exec("TRUNCATE TABLE $table")
            }
            exec("SET FOREIGN_KEY_CHECKS = 1")
        }
    }

    @Test
    fun `init need to create all tables successfully`() {
        dbController.init()

        assertDoesNotThrow { dbController.init() }
    }

    
    @Test
    fun `getUserByPage should return users with default parameters`() {
        val users = createMultipleTestUsers(5)
        users.forEach { dbController.insertUser(it) }

        val result = dbController.getUserByPage(1, 10)
        assertEquals(5, result.size)
    }

    @Test
    fun `getUserByPage with price filters should work correctly`() {
        val cheapUser = createTestUser( "cheap", price = 500)
        val expensiveUser = createTestUser("expensive", price = 2000)
        val midUser = createTestUser("mid", price = 1000)

        listOf(cheapUser, expensiveUser, midUser).forEach { dbController.insertUser(it) }

        val result = dbController.getUserByPage(1, 10, priceMin = 600, priceMax = 1500)
        assertEquals(1, result.size)
        assertEquals("mid", result[0].login)
    }

    @Test
    fun `getUserById should return correct user`() {
        val user = createTestUser( "testuser")
        val userId = dbController.insertUser(user)

        val result = dbController.getUserById(userId)
        assertEquals("+7 (4852) 73-88-15", result?.phone)
    }

    @Test
    fun `getUserById should return null for non-existent user`() {
        assertNull(dbController.getUserById(999999))
    }

    @Test
    fun `getUserByEmail should work correctly`() {
        val user = createTestUser( "testuser")
        dbController.insertUser(user)

        val result = dbController.getUserByEmail("unique@example.com")
        assertEquals("testuser", result?.login)
    }

    @Test
    fun `getUserByLogin should work correctly`() {
        val user = createTestUser("uniquelogin")
        dbController.insertUser(user)

        val result = dbController.getUserByLogin("uniquelogin")
        assertEquals("+7 (4852) 73-88-15", result?.phone)
    }

    @Test
    fun `getAllUsersByRole should return users with specific role`() {
        val teacher1 = createTestUser("teacher1", roles = mutableSetOf(RoleEnums.TEACHER))
        val teacher2 = createTestUser("teacher2", roles = mutableSetOf(RoleEnums.TEACHER))
        val user = createTestUser( "user", roles = mutableSetOf(RoleEnums.USER))

        listOf(teacher1, teacher2, user).forEach { dbController.insertUser(it) }

        val teachers = dbController.getAllUsersByRole(RoleEnums.TEACHER.id)
        assertEquals(2, teachers.size)
        assertTrue(teachers.all { it.roles == mutableSetOf(RoleEnums.TEACHER )})
    }

    @Test
    fun `updateTeacherRequest should work correctly`() {
        val pendingTeacher = createTestUser("pending", roles = mutableSetOf(RoleEnums.PENDING_TEACHER))
        val pendingId = dbController.insertUser(pendingTeacher)

        assertTrue(dbController.updateTeacherRequest(pendingId, true))
        assertEquals(RoleEnums.TEACHER.id, dbController.getUserById(pendingId)?.id)
    }

    @Test
    fun `confirmUser should work correctly`() {
        val user = createTestUser("unconfirmed", isConfirmed = false)
        val userId = dbController.insertUser(user)

        assertTrue(dbController.confirmUser(userId))
        assertTrue(dbController.getUserById(userId)?.isConfirmed == true)
    }

    @Test
    fun `insertUser should return valid user ID`() {
        val user = createTestUser( "newuser")
        val userId = dbController.insertUser(user)
        assertTrue(userId > 0)
    }

    @Test
    fun `updateUserInfo should update user data`() {
        val user = createTestUser("olduser")
        val userId = dbController.insertUser(user)

        val updatedData = user.copy(
            name = "NewFirst",
            phone = "new@test.com"
        )

        assertTrue(dbController.updateUserInfo(userId, updatedData))
        val retrieved = dbController.getUserById(userId)
        assertEquals("NewFirst", retrieved?.name)
    }
    
    @Test
    fun `getSpotsByPage should return spots with default parameters`() {
        val spots = createMultipleTestSpots(5)
        spots.forEach { dbController.insertSpot(it) }

        val result = dbController.getSpotsByPage(1, 10)
        assertEquals(5, result.size)
    }

    @Test
    fun `spotAddDayOccupation should add multiple days to spot successfully`() {

        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val spotId = dbController.insertSpot(spot)
        val dates = listOf(
            LocalDate(2024, 1, 15),
            LocalDate(2024, 1, 16)
        )
        val dayIds = dates.map { dbController.insertDayOccupation(it) }


        val result = dbController.spotAddDayOccupation(spotId, dayIds)


        assertTrue(result)
    }

    @Test
    fun `spotAddDayOccupation should add single day to spot successfully`() {

        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val spotId = dbController.insertSpot(spot)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)


        val result = dbController.spotAddDayOccupation(spotId, dayId)


        assertTrue(result)
    }

    @Test
    fun `occupyHour should return false for non-existent user`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hour = 14
        dbController.insertHourOccupation(hour, dayId)


        val result = dbController.occupyHour(dayId, hour, 999999)


        assertFalse(result)
    }

    @Test
    fun `insertHourGroupOccupation should handle empty hours list`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hours = emptyList<Int>()


        val hourIds = dbController.insertHourGroupOccupation(hours, dayId)


        assertTrue(hourIds.isEmpty())
    }

    @Test
    fun `insertHourGroupOccupation should create multiple hour occupations with date target`() {

        val date = LocalDate(2024, 1, 15)
        dbController.insertDayOccupation(date)
        val hours = listOf(14, 15, 16)


        val hourIds = dbController.insertHourGroupOccupation(hours, date)


        assertEquals(3, hourIds.size)
        hourIds.forEach { assertTrue(it > 0) }
    }

    fun `insertHourGroupOccupation should create multiple hour occupations with day id`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hours = listOf(10, 11, 12)


        val hourIds = dbController.insertHourGroupOccupation(hours, dayId)


        assertEquals(3, hourIds.size)
        hourIds.forEach { assertTrue(it > 0) }
    }

    @Test
    fun `insertHourOccupation should create hour occupation with date target`() {

        val date = LocalDate(2024, 1, 15)
        dbController.insertDayOccupation(date)
        val hour = 14


        val hourId = dbController.insertHourOccupation(hour, date)


        assertTrue(hourId > 0)
    }

    @Test
    fun `insertHourOccupation should create hour occupation with day id`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hour = 14


        val hourId = dbController.insertHourOccupation(hour, dayId)


        assertTrue(hourId > 0)
    }

    @Test
    fun `getDayOccupationById should return correct day occupation`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)


        val dayOccupation = dbController.getDayOccupationById(dayId)


        assertEquals(dayId, dayOccupation.id)
    }

    @Test
    fun `insertDayOccupation should create day occupation and return valid id`() {

        val date = LocalDate(2024, 1, 15)


        val dayId = dbController.insertDayOccupation(date)


        assertTrue(dayId > 0)
    }

    @Test
    fun `userAddSpot should return false for non-existent user`() {
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val spotId = dbController.insertSpot(spot)

        val result = dbController.userAddSpot(999999, spotId)

        assertFalse(result)
    }

    @Test
    fun `updateSpotInfo should return false for non-existent spot`() {

        val spot = Spot(id = 999999, name = "Non-existent", description = "None")

        val result = dbController.updateSpotInfo(999999, spot)

        assertFalse(result)
    }

    @Test
    fun `updateSpotInfo should modify spot data successfully`() {
        val spot = Spot(id = 0, name = "Original Spot", description = "Original Description")
        val spotId = dbController.insertSpot(spot)
        val updatedSpot = Spot(id = spotId, name = "Updated Spot", description = "Updated Description")

        val result = dbController.updateSpotInfo(spotId, updatedSpot)

        assertTrue(result)
        val retrievedSpot = dbController.getSpotById(spotId)
        assertEquals("Updated Spot", retrievedSpot?.name)
        assertEquals("Updated Description", retrievedSpot?.description)
    }

    @Test
    fun `getSpotById should return correct spot`() {
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val spotId = dbController.insertSpot(spot)

        val retrievedSpot = dbController.getSpotById(spotId)

        assertEquals(spotId, retrievedSpot?.id)
        assertEquals("Test Spot", retrievedSpot?.name)
        assertEquals("Test Description", retrievedSpot?.description)
    }

    @Test
    fun `insertSpot should create spot and return valid id`() {
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")

        val spotId = dbController.insertSpot(spot)

        assertTrue(spotId > 0)
    }

    @Test
    fun `updateUserInfo should return false for non-existent user`() {
        val user = User(id = 999999, name = "Non-existent", phone = "+9999999999")

        val result = dbController.updateUserInfo(999999, user)

        assertFalse(result)
    }
    @Test
    fun `date operations should handle various date formats`() {
        val dates = listOf(
            LocalDate(2024, 1, 1),
            LocalDate(2024, 2, 29),
            LocalDate(2024, 12, 31),
            LocalDate(1900, 1, 1),
            LocalDate(2099, 12, 31)
        )


        dates.forEach { date ->
            assertDoesNotThrow {
                val dayId = dbController.insertDayOccupation(date)
                assertTrue(dayId > 0)
            }
        }
    }
    @Test
    fun `insertHourOccupation should handle boundary hour values`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)

        val minHourId = dbController.insertHourOccupation(0, dayId)
        val maxHourId = dbController.insertHourOccupation(23, dayId)

        assertTrue(minHourId > 0)
        assertTrue(maxHourId > 0)
    }

    @Test
    fun `spotRemoveDayOccupation should remove multiple days from spot successfully`() {

        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val spotId = dbController.insertSpot(spot)
        val dates = listOf(
            LocalDate(2024, 1, 15),
            LocalDate(2024, 1, 16)
        )
        val dayIds = dates.map { dbController.insertDayOccupation(it) }
        dbController.spotAddDayOccupation(spotId, dayIds)


        val result = dbController.spotRemoveDayOccupation(spotId, dayIds)


        assertTrue(result)
    }

    @Test
    fun `spotRemoveDayOccupation should remove single day from spot successfully`() {

        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val spotId = dbController.insertSpot(spot)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        dbController.spotAddDayOccupation(spotId, dayId)


        val result = dbController.spotRemoveDayOccupation(spotId, dayId)


        assertTrue(result)
    }



    @Test
    fun `getSpotsByPage with filters should work correctly`() {
        // 1. Очистка базы перед тестом
        dbController.dropTables()
        dbController.init()

        // 2. Создание тестовых данных БЕЗ указания ID
        val spotWithDrums = createTestSpot(hasDrums = true).apply {
            name = "Студия с барабанами"
        }
        val spotWithoutDrums = createTestSpot(hasDrums = false).apply {
            name = "Студия без барабанов"
        }
        val spotWithGuitarAmps = createTestSpot(guitarAmps = 2).apply {
            name = "Студия с 2 усилителями"
        }

        // 3. Вставка с получением реальных ID
        val spot1Id = dbController.insertSpot(spotWithDrums)
        val spot2Id = dbController.insertSpot(spotWithoutDrums)
        val spot3Id = dbController.insertSpot(spotWithGuitarAmps)

        // 4. Проверка корректности вставки
        assertNotNull(dbController.getSpotById(spot1Id))
        assertNotNull(dbController.getSpotById(spot2Id))
        assertNotNull(dbController.getSpotById(spot3Id))

        // 5. Тестирование фильтров с разными комбинациями
        val allSpots = dbController.getSpotsByPage(1, 10)
        assertEquals(3, allSpots.size, "Должны вернуться все 3 спота без фильтров")

        // 5.1 Фильтр по барабанам (true)
        val drumsSpots = dbController.getSpotsByPage(1, 10, drums = true)
        assertEquals(1, drumsSpots.size, "Должен вернуться 1 спот с барабанами")
        assertEquals("Студия с барабанами", drumsSpots[0].name)

        // 5.2 Фильтр по барабанам (false)
        val noDrumsSpots = dbController.getSpotsByPage(1, 10, drums = false)
        assertEquals(2, noDrumsSpots.size, "Должны вернуться 2 спота без барабанов")
        assertTrue(noDrumsSpots.any { it.name == "Студия без барабанов" })
        assertTrue(noDrumsSpots.any { it.name == "Студия с 2 усилителями" })

        // 5.3 Фильтр по гитарным усилителям
        val guitarAmpSpots = dbController.getSpotsByPage(1, 10, guitarAmps = 2)
        assertEquals(1, guitarAmpSpots.size, "Должен вернуться 1 спот с 2 усилителями")
        assertEquals("Студия с 2 усилителями", guitarAmpSpots[0].name)

        // 5.4 Комбинированный фильтр
        val combinedFilterSpots = dbController.getSpotsByPage(
            page = 1,
            limit = 10,
            drums = false,
            guitarAmps = 2
        )
        assertEquals(1, combinedFilterSpots.size, "Должен вернуться 1 спот без барабанов и с 2 усилителями")
        assertEquals("Студия с 2 усилителями", combinedFilterSpots[0].name)
    }

    @Test
    fun `getAllSpots should return all spots`() {
        val spots = createMultipleTestSpots(3)
        spots.forEach { dbController.insertSpot(it) }

        assertEquals(3, dbController.getAllSpots().size)
    }


    @Test
    fun `insertSpot should return valid spot ID`() {
        val spot = createTestSpot("test")
        val spotId = dbController.insertSpot(spot)
        assertTrue(spotId > 0)
    }

    @Test
    fun `deleteSpot should remove spot`() {
        val spot = createTestSpot("test")
        val spotId = dbController.insertSpot(spot)

        assertTrue(dbController.deleteSpot(spotId))
        assertNull(dbController.getSpotById(spotId))
    }

    @Test
    fun `updateSpotInfo should update spot data`() {
        val spot = createTestSpot("test")
        val spotId = dbController.insertSpot(spot)

        val updatedData = spot.copy(name = "New Name")
        assertTrue(dbController.updateSpotInfo(spotId, updatedData))
        assertEquals("New Name", dbController.getSpotById(spotId)?.name)
    }
    
    @Test
    fun `user spot operations should work correctly`() {
        val user = createTestUser("user")
        val spot1 = createTestSpot("test1")
        val spot2 = createTestSpot("test2")

        val userId = dbController.insertUser(user)
        val spot1Id = dbController.insertSpot(spot1)
        val spot2Id = dbController.insertSpot(spot2)

        assertTrue(dbController.userAddSpot(userId, spot1Id))
        assertTrue(dbController.userAddSpot(userId, listOf(spot2Id)))
        assertTrue(dbController.userRemoveSpot(userId, spot1Id))
    }
    
    @Test
    fun `user day occupation operations should work correctly`() {
        val user = createTestUser("user")
        val userId = dbController.insertUser(user)

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)

        assertTrue(dbController.userAddDayOccupation(userId, dayId))
        assertTrue(dbController.userRemoveDayOccupation(userId, dayId))
    }


    @Test
    fun `insertDayOccupation should create day occupation`() {
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        assertTrue(dayId > 0)
    }

    @Test
    fun `insertHourOccupation with dateId should work correctly`() {
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)

        val hourId = dbController.insertHourOccupation(14, dayId)
        assertTrue(hourId > 0)
    }

    @Test
    fun `insertHourGroupOccupation with dateId should work correctly`() {
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)

        val hourIds = dbController.insertHourGroupOccupation(listOf(10, 11, 12), dayId)
        assertEquals(3, hourIds.size)
    }

    @Test
    fun `occupyHour should work correctly`() {
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        dbController.insertHourOccupation(14, dayId)

        val user = createTestUser("user")
        val userId = dbController.insertUser(user)

        assertTrue(dbController.occupyHour(dayId, 14, userId))
    }

    @Test
    fun `unoccupyHour should work correctly`() {
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        dbController.insertHourOccupation(14, dayId)

        val user = createTestUser( "user")
        val userId = dbController.insertUser(user)
        dbController.occupyHour(dayId, 14, userId)

        assertTrue(dbController.unoccupyHour(dayId, 14, userId))
    }
    
    @Test
    fun `operations should handle SQL injection attempts`() {
        val name = "test'; DROP TABLE Users; --"
        val user = createTestUser(name = name)

        assertDoesNotThrow { dbController.insertUser(user) }
        assertTrue(dbController.getUserByPage(1, 10).isNotEmpty())
    }

    @Test
    fun `operations should handle special characters correctly`() {
        val user = createTestUser("тест")
        val userId = dbController.insertUser(user)

        val retrieved = dbController.getUserById(userId)
        assertEquals("test@тест.com", retrieved?.phone)
    }


    @Test
    fun `numeric operations should handle edge values correctly`() {
        val user = createTestUser("user", price = Int.MAX_VALUE)
        val userId = dbController.insertUser(user)
        assertEquals(Int.MAX_VALUE, dbController.getUserById(userId)?.price)
    }

    @Test
    fun `getUserByPage should handle edge cases`() {
        assertTrue(dbController.getUserByPage(10, 10).isEmpty())
    }
    
    @Test
    fun `spot owner operations should work correctly`() {
        val spot = createTestSpot("test")
        val user = createTestUser("user")

        val spotId = dbController.insertSpot(spot)
        val userId = dbController.insertUser(user)

        assertTrue(dbController.spotAddOwner(spotId, userId))
        assertTrue(dbController.spotRemoveOwner(spotId, userId))
    }


    @Test
    fun `spot day occupation operations should work correctly`() {
        val spot = createTestSpot("test")
        val spotId = dbController.insertSpot(spot)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)

        assertTrue(dbController.spotAddDayOccupation(spotId, dayId))
        assertTrue(dbController.spotRemoveDayOccupation(spotId, dayId))
    }
    
    @Test
    fun `removeTeacherRoleById should demote teacher to regular user`() {
        val teacher = createTestUser("teacher", roles = mutableSetOf(RoleEnums.TEACHER))
        val teacherId = dbController.insertUser(teacher)

        assertTrue(dbController.removeTeacherRoleById(teacherId))
        assertEquals(mutableSetOf(RoleEnums.USER), dbController.getUserById(teacherId)?.roles)
    }
    
    @Test
    fun `pagination should work with large dataset`() {
        val users = createMultipleTestUsers(100)
        users.forEach { dbController.insertUser(it) }

        val page1 = dbController.getUserByPage(1, 25)
        val page2 = dbController.getUserByPage(2, 25)
        val page5 = dbController.getUserByPage(5, 25)

        assertEquals(25, page1.size)
        assertEquals(25, page2.size)
        assertEquals(0, page5.size)
    }
    
    @Test
    fun `updateUserPassword should change password`() {
        val user = createTestUser("user")
        val userId = dbController.insertUser(user)

        assertTrue(dbController.updateUserPassword(userId, "newpassword"))
    }
    @Test
    fun `filter operations should handle extreme filter values`() {
        val user = createTestUser("test", price = 1000)
        dbController.insertUser(user)

        val result = dbController.getUserByPage(1, 10, priceMin = Int.MIN_VALUE, priceMax = Int.MAX_VALUE)
        assertEquals(1, result.size)
    }
}