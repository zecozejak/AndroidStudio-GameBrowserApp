package com.s151915.boardgamecollector

import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.io.File

class BGGQueries {
    companion object {
        suspend fun getUser(username: String, filesDir: File, db: SQLiteDatabase): LinkedHashMap<String, Int>? {
            val xmlDirectory = File("$filesDir/XML")
            if (!xmlDirectory.exists()) xmlDirectory.mkdir()
            val fileName = "$xmlDirectory/user.xml"
            var counts = LinkedHashMap<String, Int>()

            counts["gamecount"] = 0
            counts["addscount"] = 0

            FileManager.downloadFile(URL("https://boardgamegeek.com/xmlapi2/collection?username=$username"), "user.xml", filesDir)
            val results: ArrayList<LinkedHashMap<String, String>>? = XmlParser.parseCollection(fileName)
            if (results != null) {
                SqlQueries.clearAllGameInfo(db)
                for (res in results) {
                    if (res["type"] == "boardgame") {
                        counts["gamecount"] = counts["gamecount"]!! + 1
                    }
                    else {
                        counts["addscount"] = counts["addscount"]!! + 1
                    }
                    //FileManager.downloadFile(URL("https://boardgamegeek.com/xmlapi2/thing?id=${res["id"]}&stats=1"), "item.xml", filesDir)
                    //val itemFileName = "$xmlDirectory/item.xml"
                    //val stats = XmlParser.parseGameInfo(itemFileName)
                    if (!res.containsKey("year")) res["year"] = "0"
                    if (!res.containsKey("type")) res["type"] = "0"
                    if (!res.containsKey("thumbnail")) res["thumbnail"] = "0"
                    if (!res.containsKey("image")) res["image"] = "0"
                    SqlQueries.insertGameInfo(res["name"], res["type"], res["year"], res["id"], res["thumbnail"], res["image"], db)
                }
            }
            else {
                return null
            }
            return counts
        }
    }
}