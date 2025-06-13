package ru.yarsu.db

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.yarsu.web.domain.classes.Spot
import ru.yarsu.web.domain.classes.User
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Testcontainers
class DatabaseControllerTest {

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
    fun `init should create all tables successfully`() {
        dbController.init()

        assertDoesNotThrow { dbController.init() }
    }

    @Test
    fun `dropTables should remove all tables successfully`() {
        dbController.init()

        assertDoesNotThrow { dbController.dropTables() }
    }

    @Test
    fun `insertUser should create user and return valid id`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")

        val userId = dbController.insertUser(user)

        assertTrue(userId > 0)
    }

    @Test
    fun `getUserById should return correct user`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)

        val retrievedUser = dbController.getUserById(userId)

        assertEquals(userId, retrievedUser?.id)
        assertEquals("Test User", retrievedUser?.name)
        assertEquals("+1234567890", retrievedUser?.phone)
    }

    @Test
    fun `getUserById should throw exception for non-existent user`() {
        assertThrows<Exception> {
            dbController.getUserById(999999)
        }
    }

    @Test
    fun `getUserByPage should return correct users with pagination`() {
        val users = listOf(
            User(id = 0, name = "User1", phone = "+1111111111"),
            User(id = 0, name = "User2", phone = "+2222222222"),
            User(id = 0, name = "User3", phone = "+3333333333")
        )
        users.forEach { dbController.insertUser(it) }

        val firstPage = dbController.getUserByPage(1, 2)
        val secondPage = dbController.getUserByPage(2, 2)
        
        assertEquals(2, firstPage.size)
        assertEquals(1, secondPage.size)
    }

    @Test
    fun `getUserByPage should return empty list for invalid page`() {
        val users = dbController.getUserByPage(999, 10)
        
        assertTrue(users.isEmpty())
    }

    @Test
    fun `updateUserInfo should modify user data successfully`() {
        val user = User(id = 0, name = "Original Name", phone = "+1111111111")
        val userId = dbController.insertUser(user)
        val updatedUser = User(id = userId, name = "Updated Name", phone = "+2222222222")
        
        val result = dbController.updateUserInfo(userId, updatedUser)
        
        assertTrue(result)
        val retrievedUser = dbController.getUserById(userId)
        assertEquals("Updated Name", retrievedUser?.name)
        assertEquals("+2222222222", retrievedUser?.phone)
    }

    @Test
    fun `updateUserInfo should return false for non-existent user`() {
        val user = User(id = 999999, name = "Non-existent", phone = "+9999999999")
        
        val result = dbController.updateUserInfo(999999, user)
        
        assertFalse(result)
    }
    

    @Test
    fun `insertSpot should create spot and return valid id`() {
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        
        val spotId = dbController.insertSpot(spot)
        
        assertTrue(spotId > 0)
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
    fun `getSpotById should throw exception for non-existent spot`() {
        assertThrows<Exception> {
            dbController.getSpotById(999999)
        }
    }

    @Test
    fun `getSpotsByPage should return correct spots with pagination`() {
        val spots = listOf(
            Spot(id = 0, name = "Spot1", description = "Description1"),
            Spot(id = 0, name = "Spot2", description = "Description2"),
            Spot(id = 0, name = "Spot3", description = "Description3")
        )
        spots.forEach { dbController.insertSpot(it) }
        
        val firstPage = dbController.getSpotsByPage(1, 2)
        val secondPage = dbController.getSpotsByPage(2, 2)
        
        assertEquals(2, firstPage.size)
        assertEquals(1, secondPage.size)
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
    fun `updateSpotInfo should return false for non-existent spot`() {

        val spot = Spot(id = 999999, name = "Non-existent", description = "None")
        
        val result = dbController.updateSpotInfo(999999, spot)
        
        assertFalse(result)
    }
    

    @Test
    fun `userAddSpot should add single spot to user successfully`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val userId = dbController.insertUser(user)
        val spotId = dbController.insertSpot(spot)
        
        val result = dbController.userAddSpot(userId, spotId)
        
        assertTrue(result)
    }

    @Test
    fun `userAddSpot should add multiple spots to user successfully`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val spots = listOf(
            Spot(id = 0, name = "Spot1", description = "Description1"),
            Spot(id = 0, name = "Spot2", description = "Description2")
        )
        val userId = dbController.insertUser(user)
        val spotIds = spots.map { dbController.insertSpot(it) }
        
        val result = dbController.userAddSpot(userId, spotIds)
        
        assertTrue(result)
    }

    @Test
    fun `userAddSpot should return false for non-existent user`() {
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val spotId = dbController.insertSpot(spot)
        
        val result = dbController.userAddSpot(999999, spotId)
        
        assertFalse(result)
    }

    @Test
    fun `userAddSpot should return false for non-existent spot`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)

        val result = dbController.userAddSpot(userId, 999999)
        
        assertFalse(result)
    }

    @Test
    fun `userRemoveSpot should remove single spot from user successfully`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val userId = dbController.insertUser(user)
        val spotId = dbController.insertSpot(spot)
        dbController.userAddSpot(userId, spotId)
        
        val result = dbController.userRemoveSpot(userId, spotId)
        
        assertTrue(result)
    }

    @Test
    fun `userRemoveSpot should remove multiple spots from user successfully`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val spots = listOf(
            Spot(id = 0, name = "Spot1", description = "Description1"),
            Spot(id = 0, name = "Spot2", description = "Description2")
        )
        val userId = dbController.insertUser(user)
        val spotIds = spots.map { dbController.insertSpot(it) }
        dbController.userAddSpot(userId, spotIds)
        
        val result = dbController.userRemoveSpot(userId, spotIds)
        
        assertTrue(result)
    }

    @Test
    fun `spotAddOwner should add single owner to spot successfully`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val userId = dbController.insertUser(user)
        val spotId = dbController.insertSpot(spot)
        
        val result = dbController.spotAddOwner(spotId, userId)
        
        assertTrue(result)
    }

    @Test
    fun `spotAddOwner should add multiple owners to spot successfully`() {
        val users = listOf(
            User(id = 0, name = "User1", phone = "+1111111111"),
            User(id = 0, name = "User2", phone = "+2222222222")
        )
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val userIds = users.map { dbController.insertUser(it) }
        val spotId = dbController.insertSpot(spot)
        
        val result = dbController.spotAddOwner(spotId, userIds)
        
        assertTrue(result)
    }

    @Test
    fun `spotRemoveOwner should remove single owner from spot successfully`() {
        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val userId = dbController.insertUser(user)
        val spotId = dbController.insertSpot(spot)
        dbController.spotAddOwner(spotId, userId)
        
        val result = dbController.spotRemoveOwner(spotId, userId)
        
        assertTrue(result)
    }

    @Test
    fun `spotRemoveOwner should remove multiple owners from spot successfully`() {
        val users = listOf(
            User(id = 0, name = "User1", phone = "+1111111111"),
            User(id = 0, name = "User2", phone = "+2222222222")
        )
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val userIds = users.map { dbController.insertUser(it) }
        val spotId = dbController.insertSpot(spot)
        dbController.spotAddOwner(spotId, userIds)
        
        val result = dbController.spotRemoveOwner(spotId, userIds)
        
        assertTrue(result)
    }
    

    @Test
    fun `insertDayOccupation should create day occupation and return valid id`() {

        val date = LocalDate(2024, 1, 15)


        val dayId = dbController.insertDayOccupation(date)


        assertTrue(dayId > 0)
    }

    @Test
    fun `getDayOccupationById should return correct day occupation`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)


        val dayOccupation = dbController.getDayOccupationById(dayId)


        assertEquals(dayId, dayOccupation.id)
    }

    @Test
    fun `getDayOccupationById should throw exception for non-existent day`() {
        assertThrows<Exception> {
            dbController.getDayOccupationById(999999)
        }
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
    fun `insertHourOccupation should create hour occupation with date target`() {

        val date = LocalDate(2024, 1, 15)
        dbController.insertDayOccupation(date)
        val hour = 14


        val hourId = dbController.insertHourOccupation(hour, date)


        assertTrue(hourId > 0)
    }

    @Test
    fun `insertHourGroupOccupation should create multiple hour occupations with day id`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hours = listOf(10, 11, 12)


        val hourIds = dbController.insertHourGroupOccupation(hours, dayId)


        assertEquals(3, hourIds.size)
        hourIds.forEach { assertTrue(it > 0) }
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

    @Test
    fun `insertHourGroupOccupation should handle empty hours list`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hours = emptyList<Int>()


        val hourIds = dbController.insertHourGroupOccupation(hours, dayId)


        assertTrue(hourIds.isEmpty())
    }

    @Test
    fun `occupyHour should successfully occupy hour`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hour = 14
        dbController.insertHourOccupation(hour, dayId)


        val result = dbController.occupyHour(dayId, hour, userId)


        assertTrue(result)
    }

    @Test
    fun `occupyHour should return false for non-existent day`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)


        val result = dbController.occupyHour(999999, 14, userId)


        assertFalse(result)
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
    fun `occupyHour should return false for non-existent hour`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)


        val result = dbController.occupyHour(dayId, 25, userId)


        assertFalse(result)
    }

    @Test
    fun `unoccupyHour should successfully unoccupy hour`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hour = 14
        dbController.insertHourOccupation(hour, dayId)
        dbController.occupyHour(dayId, hour, userId)


        val result = dbController.unoccupyHour(dayId, hour, userId)


        assertTrue(result)
    }

    @Test
    fun `unoccupyHour should return false for non-occupied hour`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        val hour = 14
        dbController.insertHourOccupation(hour, dayId)


        val result = dbController.unoccupyHour(dayId, hour, userId)


        assertFalse(result)
    }

    @Test
    fun `userAddDayOccupation should add single day to user successfully`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)


        val result = dbController.userAddDayOccupation(userId, dayId)


        assertTrue(result)
    }

    @Test
    fun `userAddDayOccupation should add multiple days to user successfully`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val dates = listOf(
            LocalDate(2024, 1, 15),
            LocalDate(2024, 1, 16)
        )
        val dayIds = dates.map { dbController.insertDayOccupation(it) }


        val result = dbController.userAddDayOccupation(userId, dayIds)


        assertTrue(result)
    }

    @Test
    fun `userRemoveDayOccupation should remove single day from user successfully`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)
        dbController.userAddDayOccupation(userId, dayId)


        val result = dbController.userRemoveDayOccupation(userId, dayId)


        assertTrue(result)
    }

    @Test
    fun `userRemoveDayOccupation should remove multiple days from user successfully`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val userId = dbController.insertUser(user)
        val dates = listOf(
            LocalDate(2024, 1, 15),
            LocalDate(2024, 1, 16)
        )
        val dayIds = dates.map { dbController.insertDayOccupation(it) }
        dbController.userAddDayOccupation(userId, dayIds)


        val result = dbController.userRemoveDayOccupation(userId, dayIds)


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
    fun `insertHourOccupation should handle boundary hour values`() {

        val date = LocalDate(2024, 1, 15)
        val dayId = dbController.insertDayOccupation(date)

        val minHourId = dbController.insertHourOccupation(0, dayId)
        val maxHourId = dbController.insertHourOccupation(23, dayId)

        assertTrue(minHourId > 0)
        assertTrue(maxHourId > 0)
    }

    @Test
    fun `pagination should handle edge cases`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        dbController.insertUser(user)

        val zeroPage = dbController.getUserByPage(0, 10)
        assertTrue(zeroPage.isEmpty())

        val negativePage = dbController.getUserByPage(-1, 10)
        assertTrue(negativePage.isEmpty())

        val zeroLimit = dbController.getUserByPage(1, 0)
        assertTrue(zeroLimit.isEmpty())
    }

    @Test
    fun `operations should handle duplicate relationships gracefully`() {

        val user = User(id = 0, name = "Test User", phone = "+1234567890")
        val spot = Spot(id = 0, name = "Test Spot", description = "Test Description")
        val userId = dbController.insertUser(user)
        val spotId = dbController.insertSpot(spot)
        
        val firstAdd = dbController.userAddSpot(userId, spotId)
        val secondAdd = dbController.userAddSpot(userId, spotId)
        
        assertTrue(firstAdd)

        assertDoesNotThrow { dbController.userAddSpot(userId, spotId) }
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
}