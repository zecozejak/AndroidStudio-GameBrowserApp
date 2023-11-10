package com.s151915.boardgamecollector

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class SingleGameActivity : AppCompatActivity() {
    lateinit var mainImage: ImageView
    lateinit var idText: TextView
    lateinit var titleText: TextView
    lateinit var yearText: TextView
    lateinit var galleryButton: Button
    lateinit var cameraButton: Button
    lateinit var gamesTable: TableLayout

    lateinit var gameId: String
    lateinit var db: SQLiteDatabase
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_game)

        val dbHelper = SqlManager(this)
        db = dbHelper.writableDatabase

        mainImage = findViewById(R.id.mainImage)
        idText = findViewById(R.id.idText)
        titleText = findViewById(R.id.titleText)
        yearText = findViewById(R.id.yearText)
        galleryButton = findViewById(R.id.galleryButton)
        cameraButton = findViewById(R.id.cameraButton)
        gamesTable = findViewById(R.id.oneGameTable)

        gameId = intent.getStringExtra("id").toString()

        galleryButton.setOnClickListener { getImageGallery() }
        cameraButton.setOnClickListener { dispatchTakePictureIntent() }

        getGameInfo()
        loadUri()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            saveUri(imageUri)
            addRow(imageUri)
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            saveBitmap(imageBitmap)
        }
    }

    private fun saveUri(uri: Uri?) {
        val s = uri.toString()
        SqlQueries.insertImage(gameId, s, db)
    }

    private fun loadUri() {
        val items = SqlQueries.getImages(gameId, db)
        for (it in items) {
            val imageUri = Uri.parse(it)
            addRow(imageUri)
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val contentResolver: ContentResolver = this.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, createImageFile())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        var imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            val outputStream = contentResolver.openOutputStream(imageUri!!)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            outputStream?.flush()
            outputStream?.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        saveUri(imageUri)
        addRow(imageUri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): String {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"
        return timeStamp
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun removePhoto(uri: Uri?) {
        val s = uri.toString()
        SqlQueries.clearPhoto(s, db)
    }

    private fun removeRow(uri: Uri?, row: TableRow) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
            .setMessage("Czy na pewno chcesz usunąć to zdjęcie?")
        builder.setPositiveButton("Tak") { _, _ ->
            removePhoto(uri)
            gamesTable.removeView(row)
        }
        builder.setNegativeButton("Nie") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun addRow(uri: Uri?) {
        val newRow = TableRow(this)

        val column1 = ImageView(this).apply {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            Glide.with(this).load(uri).into(this)
        }

        newRow.addView(column1)
        gamesTable.addView(newRow)

        column1.setOnClickListener { removeRow(uri, newRow) }
    }

    private fun getImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun getGameInfo() {
        val item = SqlQueries.getSingleGameData(gameId, db)
        Glide.with(this).load(item["image"]).into(mainImage)
        idText.text = item["id"]
        titleText.text = item["title"]
        yearText.text = item["year"]
    }
}