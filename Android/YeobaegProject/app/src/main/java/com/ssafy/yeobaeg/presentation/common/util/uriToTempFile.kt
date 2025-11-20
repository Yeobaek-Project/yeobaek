package com.ssafy.yeobaeg.presentation.common.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun uriToTempFile(context: Context, uri: Uri): File {
    val name = context.contentResolver
        .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { if (it.moveToFirst()) it.getString(0) else null }
        ?: "picked_${System.currentTimeMillis()}"

    val out = File(context.cacheDir, name)
    context.contentResolver.openInputStream(uri)!!.use { input ->
        out.outputStream().use { output -> input.copyTo(output) }
    }
    return out
}