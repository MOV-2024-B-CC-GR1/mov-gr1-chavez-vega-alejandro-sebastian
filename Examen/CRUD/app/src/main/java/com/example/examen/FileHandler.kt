package com.example.examen

import android.content.Context

class FileHandler(private val context: Context) {

    fun readFile(fileName: String): List<String> {
        return context.assets.open(fileName).bufferedReader().useLines { it.toList() }
    }

    fun writeFile(fileName: String, data: List<String>) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write(data.joinToString("\n").toByteArray())
        }
    }
}
