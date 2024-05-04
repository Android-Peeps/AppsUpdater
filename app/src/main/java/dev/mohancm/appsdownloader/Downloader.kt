package dev.mohancm.appsdownloader

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import okio.buffer
import okio.sink
import java.io.File

class Downloader {
    fun downloadFile(url: String, file: File) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle download failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    val source = responseBody.source()
                    val sink = file.sink().buffer()
                    sink.writeAll(source)
                    sink.close()
                }
            }
        })
    }
}