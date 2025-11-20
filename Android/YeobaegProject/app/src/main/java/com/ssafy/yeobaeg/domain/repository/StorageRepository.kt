package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.storage.PresignedUrl
import java.io.File

interface StorageRepository {

    suspend fun createPresignedUrl(
        folder: String,
        count: Int,
    ): Result<List<PresignedUrl>>

    suspend fun uploadWithPresignedPut(
        file: File,
        presignedUrl: String,
    ): Result<Unit>

    suspend fun deleteImage(
        fileKey: String
    ): Result<Unit>
}
