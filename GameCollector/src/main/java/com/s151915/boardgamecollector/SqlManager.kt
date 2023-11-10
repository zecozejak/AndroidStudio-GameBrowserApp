package com.s151915.boardgamecollector

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

class SqlManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "mydatabase.db"
        const val DATABASE_VERSION = 1


        const val GAMES_TABLE = "games_table"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TYPE = "type"
        const val COLUMN_YEAR = "year"
        const val COLUMN_BGG_ID = "bgg_id"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_IMAGE = "image"


        const val USER_TABLE = "user_table"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_GAMES = "games_num"
        const val COLUMN_ADDS = "adds_num"
        const val COLUMN_SYN_DATE = "syn_date"

        const val PHOTOS_TABLE = "photos_table"
        const val COLUMN_PHOTOS_ID = "photos_id"
        const val COLUMN_GAME_ID = "game_id"
        const val COLUMN_IMAGE_URI = "img_uri"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery1 = "CREATE TABLE $GAMES_TABLE ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_TYPE TEXT, $COLUMN_YEAR INTEGER, $COLUMN_BGG_ID INTEGER, $COLUMN_THUMBNAIL TEXT, $COLUMN_IMAGE TEXT)"
        val createTableQuery2 = "CREATE TABLE $USER_TABLE ($COLUMN_USER_ID INTEGER PRIMARY KEY, $COLUMN_USERNAME TEXT, $COLUMN_GAMES INTEGER, $COLUMN_ADDS INTEGER, $COLUMN_SYN_DATE TEXT)"
        val createTableQuery3 = "CREATE TABLE $PHOTOS_TABLE ($COLUMN_PHOTOS_ID INTEGER PRIMARY KEY, $COLUMN_GAME_ID INTEGER, $COLUMN_IMAGE_URI TEXT)"

        db.execSQL(createTableQuery1)
        db.execSQL(createTableQuery2)
        db.execSQL(createTableQuery3)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if necessary
    }
}