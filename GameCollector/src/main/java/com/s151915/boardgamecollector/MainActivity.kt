package com.s151915.boardgamecollector

import android.app.ProgressDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    lateinit var submit_button: Button
    lateinit var username_edit: EditText
    lateinit var db: SQLiteDatabase
    lateinit var response_text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = SqlManager(this)
        db = dbHelper.writableDatabase

        response_text = findViewById(R.id.response_text)
        username_edit = findViewById(R.id.username_edit)
        submit_button = findViewById(R.id.submit_button)
        submit_button.setOnClickListener { submitOnClick() }

        checkExisting()
    }

    private fun checkExisting() {
        val items = SqlQueries.getUser(db)
        if (items.isNotEmpty()) {
            intent = Intent(this@MainActivity, MainMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun submitOnClick() {
        val dialog = ProgressDialog.show(this, "", "Pobieranie. Proszę czekać...", true)
        CoroutineScope(Dispatchers.IO).launch {
            val result = SyncUser.getUser(this@MainActivity, username_edit.text.toString(), filesDir, db)
            withContext(Dispatchers.Main) {
                if (result) {
                    dialog.hide()
                    intent = Intent(this@MainActivity, MainMenuActivity::class.java)
                    startActivity(intent)
                }
                else {
                    dialog.hide()
                    response_text.text = "Nie udało się znaleźć użytkownika"
                }
            }
        }
    }
}