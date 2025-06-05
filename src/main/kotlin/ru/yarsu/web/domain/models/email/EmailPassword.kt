package ru.yarsu.web.domain.models.email

import at.favre.lib.crypto.bcrypt.BCrypt
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.models.telegram.AuthUtils
import java.time.ZoneId
import java.time.ZonedDateTime

fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray()) // если надо будет повысить безопасность, то можно взять 14, а не 12
}

fun verifyPassword(password: String, hash: String): Boolean {
    return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
}

fun createAuthCookie(user: UserModel, salt: String): Cookie {
    val rawData = "${user.id}:${user.login}"
    val signature = AuthUtils.hmacSign(rawData, salt)

    return Cookie(
        name = "auth",
        value = "$rawData:$signature",
        path = "/",
        httpOnly = true,
        secure = true,
        sameSite = SameSite.Strict,
        expires = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).plusDays(7).toInstant()
    )
}
