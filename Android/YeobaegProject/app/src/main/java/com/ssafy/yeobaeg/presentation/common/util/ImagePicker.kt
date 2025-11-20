package com.ssafy.yeobaeg.presentation.common.util

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberMultiImagePicker(
    max: Int = 10,
    onPicked: (List<Uri>) -> Unit
): () -> Unit {
    val picker33 = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(max)
    ) { uris -> onPicked(uris) }

    val pickerLegacy = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris -> onPicked(uris) }

    return {
        if (Build.VERSION.SDK_INT >= 33) {
            picker33.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            pickerLegacy.launch(arrayOf("image/*"))
        }
    }
}