package com.s151915.boardgamecollector

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.URLSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.bumptech.glide.Glide
import java.net.URLStreamHandler

class GamesActivity : AppCompatActivity() {
    private lateinit var gamesTable: TableLayout
    lateinit var alfaButton: Button
    lateinit var dateButton: Button

    private var rowsNum = 1
    private lateinit var type: String
    private var orderBy = "alfa"
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        val dbHelper = SqlManager(this)
        db = dbHelper.writableDatabase

        type = intent.getStringExtra("type").toString()

        gamesTable = findViewById(R.id.gamesTable)
        alfaButton = findViewById(R.id.alfaButton)
        dateButton = findViewById(R.id.dateButton)
        alfaButton.setOnClickListener { orderBy = "alfa"; getData() }
        dateButton.setOnClickListener { orderBy = "date"; getData() }

        getData()
    }

    private fun viewDetails(id: String?) {
        val intent = Intent(this, SingleGameActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun insertRow(item: LinkedHashMap<String, String>) {
        val newRow = TableRow(this)

        val column1 = TextView(this).apply {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            text = rowsNum.toString()
        }
        val column2 = ImageView(this).apply {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            Glide.with(this).load(item["thumbnail"]).into(this)
        }
        val column3 = TextView(this).apply {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            text = "${item["title"]} (${item["year"]})"
        }
        val spannableString = SpannableString(column3.text)
        spannableString.setSpan(URLSpan(""), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        column3.setText(spannableString, TextView.BufferType.SPANNABLE)

        column3.setOnClickListener { viewDetails(item["id"]) }

        newRow.addView(column1)
        newRow.addView(column2)
        newRow.addView(column3)

        gamesTable.addView(newRow)
        rowsNum++
    }

    private fun getData() {
        rowsNum = 1
        gamesTable.removeAllViews()
        val items = SqlQueries.getGameData(type, orderBy, db)
        for (it in items) {
            insertRow(it)
        }
    }
}