package ru.yarsu.web.domain.models.email

import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import ru.yarsu.config.AppConfig
import java.util.*

class EmailService(private val config: AppConfig) {

    private val session: Session

    init {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(config.webConfig.smtpUsername, config.webConfig.smtpPassword)
            }
        })
    }

    fun send(to: String, subject: String, content: String) {
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(config.webConfig.smtpUsername))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject)
                setContent(content, "text/html; charset=utf-8")
            }

            Transport.send(message)
            println("Письмо отправлено: $to")
        } catch (e: MessagingException) {
            println("Ошибка при отправке письма: ${e.message}")
        }
    }
}
