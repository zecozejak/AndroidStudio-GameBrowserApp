package com.s151915.boardgamecollector

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class SyncActivity : AppCompatActivity() {
    lateinit var synchButton: Button
    lateinit var synchText: TextView
    lateinit var db: SQLiteDatabase
    lateinit var synDate: LinkedHashMap<String, String>
    lateinit var answerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        val dbHelper = SqlManager(this)
        db = dbHelper.writableDatabase

        synchButton = findViewById(R.id.synchButton)
        synchText = findViewById(R.id.synchText)
        answerText = findViewById(R.id.answerText)

        synchButton.setOnClickListener { checkDate() }

        getDate()
    }

    private fun getDate() {
        synDate = SqlQueries.getUser(db)
        synchText.text = synDate["sync"]
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
            .setMessage("Synchronizacja miała miejsce mniej niż 24 godziny temu. Czy na pewno chcesz kontynuować?")
        builder.setPositiveButton("Tak") { _, _ ->
            doSync()
        }
        builder.setNegativeButton("Nie") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = Date()
        val parsedDate = dateFormat.parse(synDate["sync"])
        val diffInMillis = currentDate.time - parsedDate.time
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)

        if (diffInHours < 24) {
            showConfirmationDialog()
        }
        else {
            doSync()
        }
    }

    private fun doSync() {
        val dialog = ProgressDialog.show(this, "", "Pobieranie. Proszę czekać...", true)
        CoroutineScope(Dispatchers.IO).launch {
            val result =
                synDate["username"]?.let { SyncUser.getUser(this@SyncActivity, it, filesDir, db) }
            withContext(Dispatchers.Main) {
                if (result == true) {
                    dialog.hide()
                    answerText.text = "Pomyślnie zsynchronizowano dane"
                }
                else {
                    dialog.hide()
                    answerText.text = "Nie udało się :("
                }
            }
        }
    }
}