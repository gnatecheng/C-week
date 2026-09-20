package com.py2c.week.data

import android.content.Context
import org.json.JSONObject

class VideoCatalog(context: Context) {
    val byId: Map<String, VideoDemo> = load(context)

    private fun load(context: Context): Map<String, VideoDemo> {
        return try {
            val json = context.assets.open("vscode_demos/index.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val arr = root.getJSONArray("demos")
            buildMap {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val caps = o.getJSONArray("captions")
                    val captions = buildList {
                        for (c in 0 until caps.length()) {
                            val cap = caps.getJSONObject(c)
                            add(VideoCaption(atMs = cap.getLong("atMs"), text = cap.getString("text")))
                        }
                    }
                    val demo = VideoDemo(
                        id = o.getString("id"),
                        title = o.getString("title"),
                        subtitle = o.optString("subtitle"),
                        file = o.getString("file"),
                        captions = captions,
                    )
                    put(demo.id, demo)
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
