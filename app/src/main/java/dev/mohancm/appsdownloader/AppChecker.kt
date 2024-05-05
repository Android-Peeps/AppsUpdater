package dev.mohancm.appsdownloader

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

object AppChecker {

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isAppUpdated(context: Context, packageName: String, versionCode: Long): Boolean {
        val packageManager = context.packageManager
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode >= versionCode
            } else {
                packageInfo.versionCode >= versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getApkPackageName(context: Context, apk: File): String? {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageArchiveInfo(apk.absolutePath, 0)
            return packageInfo?.packageName
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getApkVersionCode(context: Context, apk: File): Long {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageArchiveInfo(apk.absolutePath, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode ?: 0
            } else {
                packageInfo?.versionCode?.toLong() ?: 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

}