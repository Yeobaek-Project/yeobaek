package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.StorageApiService
import com.ssafy.yeobaeg.data.model.request.PresignedUrlRequest
import com.ssafy.yeobaeg.data.model.response.toDomain
import com.ssafy.yeobaeg.domain.repository.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageApiService: StorageApiService,
) : StorageRepository {

    override suspend fun createPresignedUrl(
        folder: String,
        count: Int
    ) = runCatching {
        val response = storageApiService.postPresignedUrl(PresignedUrlRequest(folder, count))
        response.data?.presignedUrls?.map { it.toDomain() } ?: throw Exception(response.message)
    }

    override suspend fun uploadWithPresignedPut(
        file: File,
        presignedUrl: String,
    ): Result<Unit> {
        val body = file.asRequestBody()

        val req = Request.Builder()
            .url(presignedUrl)
            .put(body)
            .header("Content-Type", guessMime(file))
            .build()

        return withContext(Dispatchers.IO) {
            OkHttpClient().newCall(req).execute().use { resp ->
                Log.d("upload S3 http request", resp.message)
                if (resp.isSuccessful) Result.success(Unit)
                else Result.failure(Exception("S3 업로드 실패"))
            }
        }
    }

    override suspend fun deleteImage(fileKey: String) =
        runCatching {
            val response = storageApiService.deleteFile(fileKey)
            response.data ?: throw Exception(response.message)
        }

    private fun guessMime(file: File): String = when (file.extension.lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        "heic" -> "image/heic"
        "heif" -> "image/heif"
        else -> "application/octet-stream"
    }

}