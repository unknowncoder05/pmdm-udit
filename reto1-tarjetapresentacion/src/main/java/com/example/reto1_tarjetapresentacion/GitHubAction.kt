package com.example.reto1_tarjetapresentacion

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

internal fun githubIntent(url: String): Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

internal fun openGitHub(context: Context, url: String) {
    try {
        context.startActivity(githubIntent(url))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, R.string.no_browser_available, Toast.LENGTH_LONG).show()
    }
}
