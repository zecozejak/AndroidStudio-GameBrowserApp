package com.s151915.boardgamecollector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URL


class FileManager {
    companion object {
        private fun writeToFile(text: String, name: String, filesDir: File) {
            val xmlDirectory = File("$filesDir/XML")
            if (!xmlDirectory.exists()) xmlDirectory.mkdir()
            val fileName = "$xmlDirectory/$name"

            var writer = FileOutputStream(File(fileName))
            writer.write(text.toByteArray())
            writer.close()
        }

        fun downloadFile(url: URL, name: String, filesDir: File) {
            try {
                val buff = BufferedReader(
                    InputStreamReader(
                        url.openStream()
                    )
                )
                var text: String = ""
                var inputLine: String?
                while (buff.readLine().also { inputLine = it } != null) text += inputLine
                buff.close()
                writeToFile(text, name, filesDir)

            } catch (e: Exception) {

            }
        }
    }
}
