package dev.mohancm.appsdownloader

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class Installer {
    fun installApk(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val filUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider",  file)
        intent.setDataAndType(filUri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }
}