package ru.yarsu.web.utils

import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO

object ImageUtils {
    fun generateSafeWebpFilename(
        prefix: String,
        id: Any,
    ): String {
        val timestamp = Instant.now().epochSecond
        val uniqueId = UUID.randomUUID().toString().take(8)
        return "${prefix}_${id}_${timestamp}_$uniqueId.webp"
    }

    fun saveImageAsWebP(
        inputStream: InputStream,
        outputPath: String,
    ) {
        val originalImage: BufferedImage =
            ImageIO.read(inputStream)
                ?: throw IllegalArgumentException("Невозможно прочитать изображение")

        val writers = ImageIO.getImageWritersByFormatName("webp")
        if (!writers.hasNext()) {
            throw IllegalStateException("WebP writer не найден. Убедитесь, что библиотека imageio-webp подключена.")
        }

        val writer = writers.next()
        val outputFile = File(outputPath)
        outputFile.outputStream().use { os ->
            val ios = ImageIO.createImageOutputStream(os)
            writer.output = ios
            writer.write(null, javax.imageio.IIOImage(originalImage, null, null), null)
            writer.dispose()
        }
    }
}
