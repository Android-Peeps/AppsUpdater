package dev.mohancm.appsdownloader

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import dev.mohancm.appsdownloader.AppChecker.getApkPackageName
import dev.mohancm.appsdownloader.AppChecker.getApkVersionCode
import dev.mohancm.appsdownloader.Downloader.downloadFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException


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
            lifecycleScope.launch(Dispatchers.IO) {
                downloadAndInstallApps()
            }
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
                val responseBody = response.body ?: return
                val apps = JsonParser.parseJson(responseBody.string())
                for ((appName, app) in apps) {
                    val file = File(filesDir, "$appName.apk")
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (file.exists()) {
                            val apkIsValid = getApkPackageName(
                                context = this@MainActivity,
                                apk = file
                            ) != null
                            val apkIsLatest = getApkVersionCode(
                                context = this@MainActivity,
                                apk = file
                            ) == app.versionCode.toLong()
                            if (apkIsValid && apkIsLatest) {
                                onDownloadComplete(app, file)
                            } else {
                                downloadFile(
                                    url = app.url,
                                    file = file,
                                    onDownloadComplete = { file ->
                                        onDownloadComplete(app, file)
                                    }
                                )
                            }
                        } else {
                            downloadFile(app.url, file, onDownloadComplete = { file ->
                                onDownloadComplete(app, file)
                            })
                        }
                    }
                }
            }

        })
    }

    private fun onDownloadComplete(app: App, file: File) {
        val apkPackageName = getApkPackageName(this@MainActivity, file)
        if (apkPackageName != null) {
            val apkInstalled = AppChecker.isAppInstalled(
                this@MainActivity,
                apkPackageName
            )
            val apkUpdated = AppChecker.isAppUpdated(
                this@MainActivity,
                apkPackageName,
                app.versionCode.toLong()
            )
            if (apkInstalled && apkUpdated) {
                Log.w("AppInstaller", "App $apkPackageName is already installed and updated")
            } else {
                Installer.installApk(this@MainActivity, file)
            }
        }
    }
}