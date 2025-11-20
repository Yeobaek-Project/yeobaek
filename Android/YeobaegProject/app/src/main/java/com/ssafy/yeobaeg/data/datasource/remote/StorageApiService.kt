package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.request.PresignedUrlRequest
import com.ssafy.yeobaeg.data.model.response.PresignedUrlData
import com.ssafy.yeobaeg.data.model.response.PresignedUrlResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface StorageApiService {

    @POST("storage/presigned-urls")
    suspend fun postPresignedUrl(
        @Body request: PresignedUrlRequest
    ): BaseResponse<PresignedUrlResponse>

    @DELETE("storage/files")
    suspend fun deleteFile(
        @Query("fileKey") fileKey: String
    ): BaseResponse<Unit>
}