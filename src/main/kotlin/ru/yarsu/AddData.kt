package ru.yarsu

import kotlinx.datetime.LocalDate
import ru.yarsu.db.DatabaseController
import ru.yarsu.db.databasecontrollers.OccupationsController
import ru.yarsu.web.domain.classes.Spot
import ru.yarsu.web.domain.classes.User
import ru.yarsu.web.domain.enums.DistrictEnums
import ru.yarsu.web.domain.enums.*
import ru.yarsu.web.domain.models.email.hashPassword

val users = listOf(
    // Учителя (10)
    User(
        id = 1,
        name = "Иван Петров",
        tg_id = 100000001,
        login = "teacher1",
        password = "pass1",
        phone = "+79100000001",
        experience = 5,
        abilities = mutableSetOf(AbilityEnums.GUITAR, AbilityEnums.VOICE),
        price = 1000,
        description = "Опытный преподаватель гитары и вокала",
        district = DistrictEnums.LENINSKY,
        images = listOf("profile1.jpg"),
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 2,
        name = "Алексей Смирнов",
        tg_id = 100000002,
        login = "teacher2",
        password = "pass2",
        phone = "+79100000002",
        experience = 7,
        abilities = mutableSetOf(AbilityEnums.DRUMS),
        price = 1200,
        description = "Профессиональный барабанщик",
        district = DistrictEnums.KIROVSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 3,
        name = "Елена Иванова",
        tg_id = 100000003,
        login = "teacher3",
        password = "pass3",
        phone = "+79100000003",
        experience = 3,
        abilities = mutableSetOf(AbilityEnums.KEYBOARD),
        price = 800,
        description = "Преподаватель клавишных",
        district = DistrictEnums.FRUNZENSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 4,
        name = "Дмитрий Кузнецов",
        tg_id = 100000004,
        login = "teacher4",
        password = "pass4",
        phone = "+79100000004",
        experience = 10,
        abilities = mutableSetOf(AbilityEnums.BASS, AbilityEnums.GUITAR),
        price = 1500,
        description = "Гитарист и бас-гитарист с большим опытом",
        district = DistrictEnums.ZAVOLZHSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 5,
        name = "Ольга Васильева",
        tg_id = 100000005,
        login = "teacher5",
        password = "pass5",
        phone = "+79100000005",
        experience = 4,
        abilities = mutableSetOf(AbilityEnums.VOICE),
        price = 900,
        description = "Вокальный педагог",
        district = DistrictEnums.DZERZHINSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 6,
        name = "Сергей Николаев",
        tg_id = 100000006,
        login = "teacher6",
        password = "pass6",
        phone = "+79100000006",
        experience = 6,
        abilities = mutableSetOf(AbilityEnums.GUITAR),
        price = 1100,
        description = "Преподаватель акустической и электрогитары",
        district = DistrictEnums.KRASNOPEREKOPSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 7,
        name = "Анна Соколова",
        tg_id = 100000007,
        login = "teacher7",
        password = "pass7",
        phone = "+79100000007",
        experience = 8,
        abilities = mutableSetOf(AbilityEnums.KEYBOARD, AbilityEnums.VOICE),
        price = 1300,
        description = "Преподаватель клавишных и вокала",
        district = DistrictEnums.LENINSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 8,
        name = "Михаил Попов",
        tg_id = 100000008,
        login = "teacher8",
        password = "pass8",
        phone = "+79100000008",
        experience = 2,
        abilities = mutableSetOf(AbilityEnums.DRUMS),
        price = 700,
        description = "Начинающий преподаватель барабанов",
        district = DistrictEnums.FRUNZENSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 9,
        name = "Татьяна Федорова",
        tg_id = 100000009,
        login = "teacher9",
        password = "pass9",
        phone = "+79100000009",
        experience = 9,
        abilities = mutableSetOf(AbilityEnums.BASS),
        price = 1400,
        description = "Профессиональный бас-гитарист",
        district = DistrictEnums.ZAVOLZHSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),
    User(
        id = 10,
        name = "Александр Морозов",
        tg_id = 100000010,
        login = "teacher10",
        password = "pass10",
        phone = "+79100000010",
        experience = 5,
        abilities = mutableSetOf(AbilityEnums.GUITAR, AbilityEnums.VOICE),
        price = 1000,
        description = "Преподаватель гитары и эстрадного вокала",
        district = DistrictEnums.KIROVSKY,
        roles = mutableSetOf(RoleEnums.TEACHER)
    ),

    // Владельцы (5)
    User(
        id = 11,
        name = "Владимир Степанов",
        tg_id = 100000011,
        login = "owner1",
        password = "pass11",
        phone = "+79100000011",
        spots = listOf(),
        roles = mutableSetOf(RoleEnums.OWNER)
    ),
    User(
        id = 12,
        name = "Екатерина Козлова",
        tg_id = 100000012,
        login = "owner2",
        password = "pass12",
        phone = "+79100000012",
        spots = listOf(),
        roles = mutableSetOf(RoleEnums.OWNER)
    ),
    User(
        id = 13,
        name = "Артем Лебедев",
        tg_id = 100000013,
        login = "owner3",
        password = "pass13",
        phone = "+79100000013",
        spots = listOf(),
        roles = mutableSetOf(RoleEnums.OWNER)
    ),
    User(
        id = 14,
        name = "Наталья Новикова",
        tg_id = 100000014,
        login = "owner4",
        password = "pass14",
        phone = "+79100000014",
        spots = listOf(),
        roles = mutableSetOf(RoleEnums.OWNER)
    ),
    User(
        id = 15,
        name = "Павел Волков",
        tg_id = 100000015,
        login = "owner5",
        password = "pass15",
        phone = "+79100000015",
        spots = listOf(),
        roles = mutableSetOf(RoleEnums.OWNER)
    ),

    // Обычные пользователи (4)
    User(
        id = 16,
        name = "Андрей Семенов",
        tg_id = 100000016,
        login = "user1",
        password = "pass16",
        phone = "+79100000016",
        roles = mutableSetOf(RoleEnums.USER)
    ),
    User(
        id = 17,
        name = "Мария Павлова",
        tg_id = 100000017,
        login = "user2",
        password = "pass17",
        phone = "+79100000017",
        roles = mutableSetOf(RoleEnums.USER)
    ),
    User(
        id = 18,
        name = "Игорь Медведев",
        tg_id = 100000018,
        login = "user3",
        password = "pass18",
        phone = "+79100000018",
        roles = mutableSetOf(RoleEnums.USER)
    ),
    User(
        id = 19,
        name = "Юлия Власова",
        tg_id = 100000019,
        login = "user4",
        password = "pass19",
        phone = "+79100000019",
        roles = mutableSetOf(RoleEnums.USER)
    ),

    // Директор (1)
    User(
        id = 20,
        name = "Антон Сидоров",
        tg_id = 100000020,
        login = "director",
        password = "pass20",
        phone = "+79100000020",
        roles = mutableSetOf(RoleEnums.DIRECTOR)
    ),

    // Админ (1)
    User(
        id = 21,
        name = "Админ Админович",
        tg_id = 100000021,
        login = "admin",
        password = "admin123",
        phone = "+79100000021",
        roles = mutableSetOf(RoleEnums.ADMIN)
    )
)

val spots = listOf(
    Spot(
        id = 1,
        name = "Рок-студия",
        price = 1500,
        hasDrums = true,
        guitarAmps = 2,
        bassAmps = 1,
        description = "Полностью оборудованная студия для репетиций",
        address = "ул. Ленина, 10",
        district = DistrictEnums.LENINSKY,
        owners = listOf()
    ),
    Spot(
        id = 2,
        name = "Музыкальный уголок",
        price = 1200,
        hasDrums = false,
        guitarAmps = 1,
        bassAmps = 1,
        description = "Уютное место для занятий",
        address = "ул. Гагарина, 15",
        district = DistrictEnums.KIROVSKY,
        owners = listOf()
    ),
    Spot(
        id = 3,
        name = "Барабанная студия",
        price = 1800,
        hasDrums = true,
        guitarAmps = 0,
        bassAmps = 0,
        description = "Специализированная студия для барабанщиков",
        address = "ул. Советская, 20",
        district = DistrictEnums.FRUNZENSKY,
        owners = listOf()
    ),
    Spot(
        id = 4,
        name = "Джаз-клуб",
        price = 2000,
        hasDrums = true,
        guitarAmps = 1,
        bassAmps = 1,
        description = "Студия с атмосферой джазового клуба",
        address = "ул. Пушкина, 5",
        district = DistrictEnums.ZAVOLZHSKY,
        owners = listOf()
    ),
    Spot(
        id = 5,
        name = "Гитарный рай",
        price = 1300,
        hasDrums = false,
        guitarAmps = 3,
        bassAmps = 1,
        description = "Идеальное место для гитаристов",
        address = "ул. Мира, 12",
        district = DistrictEnums.DZERZHINSKY,
        owners = listOf()
    ),
    Spot(
        id = 6,
        name = "Вокальная студия",
        price = 1100,
        hasDrums = false,
        guitarAmps = 1,
        bassAmps = 0,
        description = "Студия с профессиональным вокальным оборудованием",
        address = "ул. Чехова, 8",
        district = DistrictEnums.KRASNOPEREKOPSKY,
        owners = listOf()
    ),
    Spot(
        id = 7,
        name = "Бас-клуб",
        price = 1400,
        hasDrums = false,
        guitarAmps = 0,
        bassAmps = 2,
        description = "Студия для бас-гитаристов",
        address = "ул. Горького, 17",
        district = DistrictEnums.LENINSKY,
        owners = listOf()
    ),
    Spot(
        id = 8,
        name = "Клавишная студия",
        price = 1600,
        hasDrums = false,
        guitarAmps = 0,
        bassAmps = 0,
        description = "Студия с качественными синтезаторами",
        address = "ул. Толстого, 3",
        district = DistrictEnums.KIROVSKY,
        owners = listOf()
    ),
    Spot(
        id = 9,
        name = "Рок-клуб",
        price = 2200,
        hasDrums = true,
        guitarAmps = 2,
        bassAmps = 2,
        description = "Большая студия для полноценных репетиций",
        address = "ул. Маяковского, 11",
        district = DistrictEnums.FRUNZENSKY,
        owners = listOf()
    ),
    Spot(
        id = 10,
        name = "Акустическая студия",
        price = 1000,
        hasDrums = false,
        guitarAmps = 1,
        bassAmps = 0,
        description = "Тихое место для акустических занятий",
        address = "ул. Некрасова, 7",
        district = DistrictEnums.ZAVOLZHSKY,
        owners = listOf()
    ),
    Spot(
        id = 11,
        name = "Универсальная студия",
        price = 1700,
        hasDrums = true,
        guitarAmps = 2,
        bassAmps = 1,
        description = "Подходит для любых музыкальных занятий",
        address = "ул. Достоевского, 9",
        district = DistrictEnums.DZERZHINSKY,
        owners = listOf()
    )
)

fun AddData(dbControl:DatabaseController, occupationsController: OccupationsController){
    for (spot in spots){
        dbControl.insertSpot(spot)
    }
    for (user in users){
        user.password = hashPassword(user.password)
        dbControl.insertUser(user)
    }

    dbControl.userAddSpot(11, listOf(1,2,3))
    dbControl.userAddSpot(12, listOf(4,5))
    dbControl.userAddSpot(13, listOf(6))
    dbControl.userAddSpot(14, listOf(7,9,10))
    dbControl.userAddSpot(15, listOf(6,8,11))
    for (j in 1..10){
        for (i in 1..30) {
            //создаём день
            var id = occupationsController.insertDayOccupation(LocalDate(2025, 6, i))
            //вставляем свободные часы 15.00 16.00 и тд
            occupationsController.insertHourGroupOccupation(listOf(12, 13, 17, 18, 19), id)
            //вставляем это челу
            dbControl.userAddDayOccupation(j, id)
        }
    }
    for (j in 1..11){
        for (i in 1..30) {
            //создаём день
            var id = occupationsController.insertDayOccupation(LocalDate(2025, 6, i))
            //вставляем свободные часы 15.00 16.00 и тд
            occupationsController.insertHourGroupOccupation(listOf(15, 16, 17, 18, 19), id)
            //вставляем это челу
            dbControl.spotAddDayOccupation(j,id)
        }
    }
}
