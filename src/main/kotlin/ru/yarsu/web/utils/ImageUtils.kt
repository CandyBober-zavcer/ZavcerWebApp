package ru.yarsu.web.utils

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.time.Instant
import java.util.UUID
import javax.imageio.ImageIO

object ImageUtils {
    private const val MAX_WIDTH = 1920
    private const val MAX_HEIGHT = 1080

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

        val resizedImage = resizeIfNeeded(originalImage)
        val outputFile = File(outputPath)
        ImageIO.write(resizedImage, "png", outputFile)
    }

    private fun resizeIfNeeded(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height

        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return image
        }

        val widthRatio = MAX_WIDTH.toDouble() / width
        val heightRatio = MAX_HEIGHT.toDouble() / height
        val scale = minOf(widthRatio, heightRatio)

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d: Graphics2D = resizedImage.createGraphics()
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null)
        g2d.dispose()

        return resizedImage
    }
}
