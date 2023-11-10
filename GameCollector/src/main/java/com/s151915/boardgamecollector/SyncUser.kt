package com.s151915.boardgamecollector

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SyncUser {
    companion object {
        suspend fun getUser(context: Context, username: String, filesDir: File, db: SQLiteDatabase) : Boolean {
            val result = BGGQueries.getUser(username, filesDir, db)
            if (result != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentDate = Date()
                val dateString = dateFormat.format(currentDate)

                SqlQueries.clearUser(db)
                SqlQueries.insertUser(username, result["gamecount"].toString(), result["addscount"].toString(), dateString, db)
                return true
            }
            else {
                return false
            }
        }
    }
}