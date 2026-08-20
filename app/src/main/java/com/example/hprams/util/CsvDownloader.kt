package com.example.hprams.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File

object CsvDownloader {
    fun downloadCsv(context: Context, filename: String, content: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { resolverOutputStream ->
                        resolverOutputStream.write(content.toByteArray())
                    }
                    Toast.makeText(context, "CSV Report saved to Downloads folder!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to create file in Downloads", Toast.LENGTH_SHORT).show()
                }
            } else {
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, filename)
                file.writeText(content)
                Toast.makeText(context, "CSV Report saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to download CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
