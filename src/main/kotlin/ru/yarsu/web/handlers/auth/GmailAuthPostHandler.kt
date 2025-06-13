package ru.yarsu.web.handlers.auth

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import ru.yarsu.config.AppConfig
import ru.yarsu.db.UserData
import ru.yarsu.web.domain.article.UserModel
import ru.yarsu.web.domain.models.telegram.AuthUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class GmailAuthPostHandler(
    private val users: UserData,
    private val config: AppConfig,
) : HttpHandler {
    private val clientId = config.gmailConfig.clientId
    val jwkProvider =
        JwkProviderBuilder(
            java.net.URI("https://www.googleapis.com/oauth2/v3/certs").toURL(),
        ).cached(10, 24, TimeUnit.HOURS)
            .build()

    override fun invoke(request: Request): Response =
        try {
            val body = request.bodyString()
            val credential = extractToken(body)
            val decodedJWT = verifyAndDecode(credential)

            val email = decodedJWT.getClaim("email").asString()
            val name = decodedJWT.getClaim("name").asString()
            val emailVerified = decodedJWT.getClaim("email_verified").asBoolean()

            if (emailVerified != true) throw IllegalArgumentException("Email не подтверждён")

            if (!users.existsByLogin(email)) {
                users.add(UserModel(name = name, login = email, password = ""))
            }

            val user = users.findByLogin(email)!!

            Response(Status.FOUND)
                .header("Location", "/")
                .cookie(createAuthCookie(user, config.webConfig.authSalt))
        } catch (e: Exception) {
            e.printStackTrace()
            Response(Status.BAD_REQUEST).body("Ошибка авторизации через Google: ${e.message}")
        }

    private fun extractToken(body: String): String {
        val mapper = jacksonObjectMapper()
        val json = mapper.readValue<Map<String, String>>(body)
        return json["credential"] ?: throw IllegalArgumentException("credential не найден")
    }

    private fun verifyAndDecode(token: String): DecodedJWT {
        val decoded = JWT.decode(token)
        val jwk = jwkProvider.get(decoded.keyId)
        val algorithm = Algorithm.RSA256(jwk.publicKey as java.security.interfaces.RSAPublicKey, null)
        val verifier =
            JWT
                .require(algorithm)
                .withAudience(clientId)
                .acceptLeeway(5)
                .build()

        val verified = verifier.verify(token)
        val issuer = verified.issuer
        if (issuer != "https://accounts.google.com" && issuer != "accounts.google.com") {
            throw IllegalArgumentException("Неверный issuer: $issuer")
        }

        return verified
    }

    private fun createAuthCookie(
        user: UserModel,
        authSalt: String,
    ): Cookie {
        val rawData = "${user.id}:${user.login}"
        val signature = AuthUtils.hmacSign(rawData, authSalt)

        return Cookie(
            name = "auth",
            value = "$rawData:$signature",
            path = "/",
            httpOnly = true,
            secure = true,
            sameSite = SameSite.Strict,
            expires =
                ZonedDateTime
                    .of(
                        LocalDateTime.now().plusDays(7),
                        ZoneId.of("Europe/Moscow"),
                    ).toInstant(),
        )
    }
}
