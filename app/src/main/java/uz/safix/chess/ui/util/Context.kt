package uz.safix.chess.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by: androdev
 * Date: 13-07-2024
 * Time: 8:14 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

fun Context.tryOpenUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    } catch (e: Exception) {
        // ignore
    }
}