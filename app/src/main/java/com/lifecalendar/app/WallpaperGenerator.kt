package com.lifecalendar.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WallpaperGenerator {

    fun generateGoalWallpaper(context: Context, targetYear: Int, targetMonth: Int, targetDay: Int, eventTitle: String): Bitmap {
        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // 1. Black Background
        canvas.drawColor(Color.BLACK)

        // 2. Date Mathematics
        val today = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(targetYear, targetMonth, targetDay, 0, 0, 0)
        }
        val start = Calendar.getInstance().apply {
            add(Calendar.MONTH, -3) // Start date assumed 3 months ago
        }

        val totalDays = TimeUnit.MILLISECONDS.toDays(target.timeInMillis - start.timeInMillis).coerceAtLeast(1)
        val daysElapsed = TimeUnit.MILLISECONDS.toDays(today.timeInMillis - start.timeInMillis).coerceAtLeast(0)
        val daysLeft = TimeUnit.MILLISECONDS.toDays(target.timeInMillis - today.timeInMillis).coerceAtLeast(0)
        val progressPercent = ((daysElapsed.toFloat() / totalDays.toFloat()) * 100).toInt().coerceIn(0, 100)

        // 3. Draw Dot Grid
        val columns = 20
        val dotRadius = width * 0.012f
        val spacing = width * 0.04f
        val startX = (width - (columns * spacing)) / 2f
        val startY = height * 0.45f // Anti-overlap position (45% down screen)

        val paint = Paint().apply { isAntiAlias = true }

        for (i in 0 until totalDays.toInt()) {
            val row = i / columns
            val col = i % columns
            val cx = startX + (col * spacing) + dotRadius
            val cy = startY + (row * spacing) + dotRadius

            when {
                i < daysElapsed -> paint.color = Color.WHITE
                i == daysElapsed.toInt() -> paint.color = Color.parseColor("#FF4500") // Today Orange
                else -> paint.color = Color.parseColor("#2C2C2C") // Muted Grey
            }
            canvas.drawCircle(cx, cy, dotRadius, paint)
        }

        // 4. Draw Texts
        val titlePaint = Paint().apply {
            color = Color.parseColor("#8E8E93")
            textSize = width * 0.04f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(eventTitle, width / 2f, startY - 60f, titlePaint)

        val textPaint = Paint().apply {
            color = Color.parseColor("#FF4500")
            textSize = width * 0.045f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        val textY = startY + ((totalDays / columns) + 2) * spacing
        canvas.drawText("${daysLeft}d left · ${progressPercent}%", width / 2f, textY, textPaint)

        return bitmap
    }
}
