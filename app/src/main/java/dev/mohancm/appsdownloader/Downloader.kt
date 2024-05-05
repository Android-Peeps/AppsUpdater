package dev.mohancm.appsdownloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import okio.buffer
import okio.sink
import ru.gildor.coroutines.okhttp.await
import java.io.File
import kotlin.math.sin

class Downloader {
    suspend fun downloadFile(url: String, file: File, onDownloadComplete: suspend (File) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).await()
        response.body?.use { responseBody ->
            val source = responseBody.source()
            val sink = file.sink().buffer()
            sink.use {
                sink.writeAll(source)
            }
            onDownloadComplete(file)
        } ?: {
            // Handle download failure
        }
    }
}