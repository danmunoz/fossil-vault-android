package com.dmdev.fossilvaultanda.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object WebViewUtil {

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle case where no browser is available
        }
    }

    fun openPrivacyPolicy(context: Context) {
        openUrl(context, "https://fossilvault.app/privacy/")
    }

    fun openTermsOfUse(context: Context) {
        openUrl(context, "https://fossilvault.app/terms/")
    }

    fun openWebsite(context: Context) {
        openUrl(context, "https://fossilvault.app")
    }

    fun openInstagram(context: Context) {
        // Try to open Instagram app first, fallback to web
        val instagramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/fossilvault"))
        instagramIntent.setPackage("com.instagram.android")

        if (instagramIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(instagramIntent)
        } else {
            // Fallback to web browser
            openUrl(context, "https://instagram.com/fossilvault")
        }
    }

    fun openEmailContact(context: Context) {
        EmailUtil.sendFeedback(context)
    }
}