package ru.yarsu.web.domain.models.telegram

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.Map

fun isValidTelegramAuth(data: Map<String, String>, botToken: String): Boolean {
    val receivedHash = data["hash"] ?: return false

    val sortedData = data
        .filterKeys { it != "hash" }
        .toSortedMap()
        .map { "${it.key}=${it.value}" }
        .joinToString("\n")

    val secretKey = MessageDigest.getInstance("SHA-256").digest(botToken.toByteArray())
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(secretKey, "HmacSHA256"))
    val calculatedHash = mac.doFinal(sortedData.toByteArray())
    val calculatedHashHex = calculatedHash.joinToString("") { "%02x".format(it) }

    return receivedHash == calculatedHashHex
}
