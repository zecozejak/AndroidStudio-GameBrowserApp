package com.s151915.boardgamecollector

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class XmlParser {
    companion object {
        fun parseCollection(path: String): ArrayList<LinkedHashMap<String, String>>? {
            var results = ArrayList<LinkedHashMap<String, String>>()
            val inputStream: InputStream = FileInputStream(File(path))
            val xmlPullParser: XmlPullParser = Xml.newPullParser()

            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(inputStream, null)

            var eventType = xmlPullParser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = xmlPullParser.name
                        if (tagName == "item") {
                            var item = LinkedHashMap<String, String>()
                            item["id"] = (xmlPullParser.getAttributeValue(null, "objectid"))
                            item["type"] = (xmlPullParser.getAttributeValue(null, "subtype"))
                            while (eventType != XmlPullParser.END_TAG || xmlPullParser.name != "item") {
                                when (eventType) {
                                    XmlPullParser.START_TAG -> {
                                        val childTagName = xmlPullParser.name
                                        if (childTagName == "name") {
                                            item["name"] = xmlPullParser.nextText()
                                        }
                                        if (childTagName == "yearpublished") {
                                            item["year"] = xmlPullParser.nextText()
                                        }
                                        if (childTagName == "image") {
                                            item["image"] = xmlPullParser.nextText()
                                        }
                                        if (childTagName == "thumbnail") {
                                            item["thumbnail"] = xmlPullParser.nextText()
                                        }
                                    }
                                }
                                eventType = xmlPullParser.next()
                            }
                            results.add(item)
                        }
                        if (tagName == "error") {
                            return null
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }

            return results
        }

        fun parseGameInfo(path: String): LinkedHashMap<String, String> {
            var results = LinkedHashMap<String, String>()
            val inputStream: InputStream = FileInputStream(File(path))
            val xmlPullParser: XmlPullParser = Xml.newPullParser()

            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            xmlPullParser.setInput(inputStream, null)

            var eventType = xmlPullParser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = xmlPullParser.name
                        if (tagName == "item") {
                            // Iterate through the child tags within <item>
                            while (eventType != XmlPullParser.END_TAG || xmlPullParser.name != "item") {
                                when (eventType) {
                                    XmlPullParser.START_TAG -> {
                                        val childTagName = xmlPullParser.name
                                        if (childTagName == "thumbnail") {
                                            results["thumbnail"] = xmlPullParser.nextText()
                                        }
                                        if (childTagName == "image") {
                                            results["image"] = xmlPullParser.nextText()
                                        }
                                        if (childTagName == "name" && xmlPullParser.getAttributeValue(null, "type") == "primary") {
                                            results["name"] = xmlPullParser.getAttributeValue(null, "value")
                                        }
                                        if (childTagName == "yearpublished") {
                                            results["year"] = xmlPullParser.getAttributeValue(null, "value")
                                        }
                                        // Do something with the child tag
                                        Log.d("XML Parsing", "Child Tag: $childTagName")
                                    }
                                }
                                eventType = xmlPullParser.next()
                            }
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }

            return results
        }
    }
}