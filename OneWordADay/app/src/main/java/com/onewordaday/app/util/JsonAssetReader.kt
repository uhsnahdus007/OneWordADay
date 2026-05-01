package com.onewordaday.app.util

import android.content.Context

object JsonAssetReader {
    fun read(context: Context, fileName: String): String =
        context.assets.open(fileName).bufferedReader().use { it.readText() }
}
