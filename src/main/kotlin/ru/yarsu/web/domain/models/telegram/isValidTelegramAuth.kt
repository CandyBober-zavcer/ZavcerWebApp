package ru.yarsu.web.domain.models.telegram

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun isValidTelegramAuth(data: Map<String, String>, botToken: String): Boolean {
    val receivedHash = data["hash"] ?: return false

    val checkString = data
        .filterKeys { it != "hash" }
        .toSortedMap()
        .map { "${it.key}=${it.value}" }
        .joinToString("\n")

    val secretKey = MessageDigest.getInstance("SHA-256")
        .digest(botToken.toByteArray())

    val hmac = Mac.getInstance("HmacSHA256").apply {
        init(SecretKeySpec(secretKey, "HmacSHA256"))
    }

    val calculatedHashBytes = hmac.doFinal(checkString.toByteArray())
    val calculatedHashHex = calculatedHashBytes.joinToString("") { "%02x".format(it) }

    return MessageDigest.isEqual(
        receivedHash.lowercase().toByteArray(),
        calculatedHashHex.toByteArray()
    )
}
