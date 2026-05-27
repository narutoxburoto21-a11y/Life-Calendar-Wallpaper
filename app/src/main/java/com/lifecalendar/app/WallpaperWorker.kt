package com.lifecalendar.app

import android.app.WallpaperManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.IOException
import java.util.Calendar

class WallpaperWorker(val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        var bitmap: android.graphics.Bitmap? = null
        try {
            // Generates the crisp countdown image
            bitmap = WallpaperGenerator.generateGoalWallpaper(
                context = context,
                targetYear = 2026,
                targetMonth = Calendar.DECEMBER,
                targetDay = 31,
                eventTitle = "Project Deadline"
            )

            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            
            return Result.success()
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.retry()
        } finally {
            bitmap?.let {
                if (!it.isRecycled) {
                    it.recycle() // Battery and RAM optimization
                }
            }
        }
    }
}
