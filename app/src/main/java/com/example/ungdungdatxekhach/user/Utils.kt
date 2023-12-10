package com.example.ungdungdatxekhach.user

import android.content.Context
import java.io.InputStream

object Utils {

    fun readJsonFromRawResource(context: Context, resourceId: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }
}
