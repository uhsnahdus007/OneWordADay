package com.onewordaday.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.onewordaday.app.data.model.Word
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareImageGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun shareWordAsImage(word: Word) {
        val bitmap = createWordBitmap(word)
        val uri = saveBitmapToCache(bitmap)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share word").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun createWordBitmap(word: Word): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint().apply { color = 0xFF111318.toInt() }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Accent bar at top
        val accentPaint = Paint().apply { color = 0xFF4D9FFF.toInt() }
        canvas.drawRect(0f, 0f, width.toFloat(), 8f, accentPaint)

        // Word text
        val wordPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            textSize = 120f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(word.word.replaceFirstChar { it.uppercase() }, 80f, 240f, wordPaint)

        // Part of speech
        val posPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF8A94A6.toInt()
            textSize = 52f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
        canvas.drawText(word.partOfSpeech, 80f, 310f, posPaint)

        // Divider
        val divPaint = Paint().apply { color = 0xFF2C3240.toInt() }
        canvas.drawRect(80f, 350f, (width - 80).toFloat(), 354f, divPaint)

        // Definition — wrap text manually
        val defPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFE2E8F0.toInt()
            textSize = 54f
            typeface = Typeface.DEFAULT
        }
        drawWrappedText(canvas, word.definition, defPaint, 80f, 430f, width - 160f)

        // App branding at bottom
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF4D9FFF.toInt()
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("OneWordADay", 80f, 980f, brandPaint)

        return bitmap
    }

    private fun drawWrappedText(canvas: Canvas, text: String, paint: Paint, x: Float, y: Float, maxWidth: Float) {
        val words = text.split(" ")
        val lineHeight = paint.textSize * 1.4f
        var line = ""
        var currentY = y

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val bounds = Rect()
            paint.getTextBounds(testLine, 0, testLine.length, bounds)
            if (bounds.width() > maxWidth && line.isNotEmpty()) {
                canvas.drawText(line, x, currentY, paint)
                line = word
                currentY += lineHeight
                if (currentY > 900f) return
            } else {
                line = testLine
            }
        }
        if (line.isNotEmpty()) canvas.drawText(line, x, currentY, paint)
    }

    private fun saveBitmapToCache(bitmap: Bitmap): android.net.Uri {
        val dir = File(context.cacheDir, "shared_images").also { it.mkdirs() }
        val file = File(dir, "word_card.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
