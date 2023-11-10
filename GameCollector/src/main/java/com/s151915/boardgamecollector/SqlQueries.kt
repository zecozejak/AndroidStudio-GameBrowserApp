package com.s151915.boardgamecollector

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class SqlQueries {
    companion object {
        fun insertGameInfo(title: String?, type: String?, year: String?, bgg_id: String?, thumbnail: String?, image: String?, db: SQLiteDatabase): Long {
            val values = ContentValues().apply {
                put(SqlManager.COLUMN_TITLE, title)
                put(SqlManager.COLUMN_TYPE, type)
                put(SqlManager.COLUMN_YEAR, year)
                put(SqlManager.COLUMN_BGG_ID, bgg_id)
                put(SqlManager.COLUMN_THUMBNAIL, thumbnail)
                put(SqlManager.COLUMN_IMAGE, image)
            }
            return db.insert(SqlManager.GAMES_TABLE, null, values)
        }

        fun clearAllGameInfo(db: SQLiteDatabase) {
            db.delete(SqlManager.GAMES_TABLE, null, null)
        }

        fun clearUser(db: SQLiteDatabase) {
            db.delete(SqlManager.USER_TABLE, null, null)
        }

        fun clearPhoto(uri: String, db: SQLiteDatabase) {
            val selection = "${SqlManager.COLUMN_IMAGE_URI} = ?"
            val selectionArgs = arrayOf(uri)
            db.delete(SqlManager.PHOTOS_TABLE, selection, selectionArgs)
        }

        fun insertUser(username: String, gamesNum: String, addsNum: String, synDate: String, db: SQLiteDatabase): Long {
            val values = ContentValues().apply {
                put(SqlManager.COLUMN_USERNAME, username)
                put(SqlManager.COLUMN_GAMES, gamesNum)
                put(SqlManager.COLUMN_ADDS, addsNum)
                put(SqlManager.COLUMN_SYN_DATE, synDate)
            }
            return db.insert(SqlManager.USER_TABLE, null, values)
        }

        fun insertImage(gameId: String, uri: String, db: SQLiteDatabase): Long {
            val values = ContentValues().apply {
                put(SqlManager.COLUMN_GAME_ID, gameId)
                put(SqlManager.COLUMN_IMAGE_URI, uri)
            }
            return db.insert(SqlManager.PHOTOS_TABLE, null, values)
        }

        fun getImages(gameId: String, db: SQLiteDatabase): MutableList<String> {
            val projection = arrayOf(SqlManager.COLUMN_IMAGE_URI)
            val selection = "${SqlManager.COLUMN_GAME_ID} = ?"
            val selectionArgs = arrayOf(gameId)
            val cursor = db.query(SqlManager.PHOTOS_TABLE, projection, selection, selectionArgs, null, null, null)
            val items = mutableListOf<String>()
            with(cursor) {
                while (moveToNext()) {
                    items.add(getString(getColumnIndexOrThrow(SqlManager.COLUMN_IMAGE_URI)))
                }
            }
            return items
        }

        fun getUser(db: SQLiteDatabase): LinkedHashMap<String, String> {
            val projection = arrayOf(SqlManager.COLUMN_USERNAME, SqlManager.COLUMN_GAMES, SqlManager.COLUMN_ADDS, SqlManager.COLUMN_SYN_DATE)
            val cursor = db.query(SqlManager.USER_TABLE, projection, null, null, null, null, null)
            val items = LinkedHashMap<String, String>()
            with(cursor) {
                while(moveToNext()) {
                    items["username"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_USERNAME))
                    items["games"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_GAMES))
                    items["adds"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_ADDS))
                    items["sync"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_SYN_DATE))
                }
            }
            cursor.close()
            return items
        }

        fun getGameData(type: String, sort: String, db: SQLiteDatabase): ArrayList<LinkedHashMap<String, String>> {
            val projection = arrayOf(SqlManager.COLUMN_TITLE, SqlManager.COLUMN_YEAR, SqlManager.COLUMN_THUMBNAIL, SqlManager.COLUMN_BGG_ID)
            val selection = "${SqlManager.COLUMN_TYPE} = ?"
            val selectionArgs = arrayOf(type)
            val sortOrder: String
            if (sort == "alfa") {
                sortOrder = "${SqlManager.COLUMN_TITLE} ASC"
            }
            else {
                sortOrder = "${SqlManager.COLUMN_YEAR} ASC"
            }
            val cursor = db.query(SqlManager.GAMES_TABLE, projection, selection, selectionArgs, null, null, sortOrder)
            val items = ArrayList<LinkedHashMap<String, String>>()
            with(cursor) {
                while(moveToNext()) {
                    val it = LinkedHashMap<String, String>()
                    it["title"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_TITLE))
                    it["year"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_YEAR))
                    it["thumbnail"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_THUMBNAIL))
                    it["id"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_BGG_ID))
                    items.add(it)
                }
            }
            cursor.close()
            return items
        }

        fun getSingleGameData(id: String, db: SQLiteDatabase): LinkedHashMap<String, String> {
            val projection = arrayOf(SqlManager.COLUMN_TITLE, SqlManager.COLUMN_YEAR, SqlManager.COLUMN_BGG_ID, SqlManager.COLUMN_IMAGE)
            val selection = "${SqlManager.COLUMN_BGG_ID} = ?"
            val selectionArgs = arrayOf(id)
            val cursor = db.query(SqlManager.GAMES_TABLE, projection, selection, selectionArgs, null, null, null)
            val item = LinkedHashMap<String, String>()
            with(cursor) {
                while(moveToNext()) {
                    item["title"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_TITLE))
                    item["year"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_YEAR))
                    item["id"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_BGG_ID))
                    item["image"] = getString(getColumnIndexOrThrow(SqlManager.COLUMN_IMAGE))
                }
            }
            return item
        }

        fun getSynDate(db: SQLiteDatabase): String {
            val projection = arrayOf(SqlManager.COLUMN_SYN_DATE)
            val cursor = db.query(SqlManager.USER_TABLE, projection, null, null, null, null, null)
            var item = ""
            with(cursor) {
                while(moveToNext()) {
                    item = getString(getColumnIndexOrThrow(SqlManager.COLUMN_SYN_DATE))
                }
            }
            cursor.close()
            return item
        }
    }
}