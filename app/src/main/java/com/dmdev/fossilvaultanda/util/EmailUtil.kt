package com.dmdev.fossilvaultanda.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

object EmailUtil {

    fun sendFeedback(context: Context) {
        val appVersion = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }

        val deviceInfo = buildString {
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("App Version: $appVersion")
            appendLine()
            appendLine("Please describe your feedback or issue below:")
            appendLine()
        }

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("hello@fossilvault.app"))
            putExtra(Intent.EXTRA_SUBJECT, "FossilVault Android Feedback")
            putExtra(Intent.EXTRA_TEXT, deviceInfo)
        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
        } catch (e: Exception) {
            // Fallback to opening email with generic intent
            val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("mailto:support@fossilvault.app?subject=FossilVault Android Feedback&body=${Uri.encode(deviceInfo)}")
            }

            if (fallbackIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(fallbackIntent)
            }
        }
    }
}