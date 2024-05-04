package dev.mohancm.appsdownloader

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.io.File


class MainActivity : AppCompatActivity() {

    private val downloadButton by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.download_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        val builder = VmPolicy.Builder()
//        StrictMode.setVmPolicy(builder.build())
//        builder.detectFileUriExposure()
        downloadButton.setOnClickListener {
            downloadAndInstallApps()
        }

    }

    private fun downloadAndInstallApps() {
        val jsonUrl = "https://alexandria-api.fly.dev/solos/apps"
        val client = OkHttpClient()
        val request = Request.Builder().url(jsonUrl).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle JSON download failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    val jsonParser = JsonParser()
                    val apps = jsonParser.parseJson(responseBody.string())
                    val downloader = Downloader()
                    val installer = Installer()
                    for ((appName, app) in apps) {
                        val file = File(filesDir, appName + ".apk")
                        downloader.downloadFile(app.url, file)
                        installer.installApk(this@MainActivity, file)
                    }
                }
            }
        })
    }
}