package ru.yarsu.web.domain.models.telegram

import org.http4k.core.Request
import org.http4k.core.cookie.cookies
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.UserModel
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object AuthUtils {
    fun getUserFromCookie(
        request: Request,
        authSalt: String,
        users: UserData,
    ): UserModel? {
        val tgAuthCookie = request.cookies().find { it.name == "auth" } ?: return null
        val parts = tgAuthCookie.value.split(":")
        if (parts.size != 3) return null

        val userId = parts[0].toIntOrNull() ?: return null
        val username = parts[1]
        val signature = parts[2]

        val rawData = "$userId:$username"
        val expectedSignature = hmacSign(rawData, authSalt)

        if (signature != expectedSignature) return null

        val user = users.getById(userId) ?: return null
        return user
    }

    fun hmacSign(
        data: String,
        secret: String,
    ): String {
        val hmacKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(hmacKey)
        return mac
            .doFinal(data.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
