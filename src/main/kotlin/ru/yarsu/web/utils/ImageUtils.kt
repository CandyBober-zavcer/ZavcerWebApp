package ru.yarsu.web.utils

import java.io.File
import java.io.InputStream
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO

object ImageUtils {
    fun generateSafePngFilename(
        prefix: String,
        id: Any,
    ): String {
        val timestamp = Instant.now().epochSecond
        val uniqueId = UUID.randomUUID().toString().take(8)
        return "${prefix}_${id}_${timestamp}_$uniqueId.png"
    }

    fun saveImageAsPng(
        inputStream: InputStream,
        outputPath: String,
    ) {
        val originalImage =
            ImageIO.read(inputStream)
                ?: throw IllegalArgumentException("Невозможно прочитать изображение")

        val outputFile = File(outputPath)
        ImageIO.write(originalImage, "png", outputFile)
    }
}
