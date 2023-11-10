package com.s151915.boardgamecollector

import android.app.AlertDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text
import java.io.File
import kotlin.system.exitProcess

class MainMenuActivity : AppCompatActivity() {
    lateinit var usernameText: TextView
    lateinit var gamesText: TextView
    lateinit var addsText: TextView
    lateinit var synText: TextView
    lateinit var gameListButton: Button
    lateinit var addsListButton: Button
    lateinit var synButton: Button
    lateinit var clearButton: Button
    lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val dbHelper = SqlManager(this)
        db = dbHelper.writableDatabase

        usernameText = findViewById(R.id.usernameText)
        gamesText = findViewById(R.id.gamesText)
        addsText = findViewById(R.id.addsText)
        synText = findViewById(R.id.synText)
        gameListButton = findViewById(R.id.gameListButton)
        addsListButton = findViewById(R.id.addsListButton)
        synButton = findViewById(R.id.synButton)
        clearButton = findViewById(R.id.clearButton)

        getUserInfo()

        gameListButton.setOnClickListener { openGamesActivity() }
        addsListButton.setOnClickListener { openAddsActivity() }
        synButton.setOnClickListener { openSynActivity() }
        clearButton.setOnClickListener { clearData() }
    }

    override fun onBackPressed() {

    }

    private fun openGamesActivity() {
        val intent = Intent(this, GamesActivity::class.java)
        intent.putExtra("type", "boardgame")
        startActivity(intent)
    }

    private fun openAddsActivity() {
        val intent = Intent(this, GamesActivity::class.java)
        intent.putExtra("type", "boardgameexpansion")
        startActivity(intent)
    }

    private fun openSynActivity() {
        val intent = Intent(this, SyncActivity::class.java)
        startActivity(intent)
    }

    private fun doClearing() {
        val databasePath = this.getDatabasePath(SqlManager.DATABASE_NAME).path
        val file = File(databasePath)
        file.delete()
        exitProcess(0)
    }

    private fun clearData() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
            .setMessage("Czy na pewno chcesz usunąć swoje dane?")
        builder.setPositiveButton("Tak") { _, _ ->
            doClearing()
        }
        builder.setNegativeButton("Nie") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getUserInfo() {
        val info = SqlQueries.getUser(db)
        usernameText.text = info["username"]
        gamesText.text = info["games"]
        addsText.text = info["adds"]
        synText.text = info["sync"]
    }
}