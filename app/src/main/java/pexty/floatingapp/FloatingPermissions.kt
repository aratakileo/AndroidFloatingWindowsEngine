package pexty.floatingapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object FloatingPermissions {
    fun has(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun take(context: Context) {
        if (!has(context))
            context.startActivity(Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}"
                )))
    }
}