package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.profile.ArtworksResponse
import com.ssafy.yeobaeg.data.model.profile.BookmarkCafesResponse
import com.ssafy.yeobaeg.data.model.profile.GetProfileResponseDto
import com.ssafy.yeobaeg.data.model.profile.UpdateProfileRequest
import com.ssafy.yeobaeg.data.model.review.ReviewListResponse
import com.ssafy.yeobaeg.data.model.review.ReviewStatisticsResponse
import com.ssafy.yeobaeg.data.model.review.WrittenReviewListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApiService {
    @GET("users/me")
    suspend fun getProfile(
    ): Response<BaseResponse<GetProfileResponseDto>>

    @GET("users/{userId}")
    suspend fun getSomeoneProfile(
        @Path("userId") userId: Long
    ): Response<BaseResponse<GetProfileResponseDto>>

    @GET("artworks/bookmarks")
    suspend fun getBookmarkArtworkList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<ArtworksResponse>>

    @GET("cafes/bookmarks")
    suspend fun getBookmarkCafeList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<BookmarkCafesResponse>>

    @PATCH("users/me")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<BaseResponse<Unit>>

    @GET("reviews/me/statistics")
    suspend fun getMyReviewStatistics(
    ): Response<BaseResponse<ReviewStatisticsResponse>>

    @GET("reviews/me")
    suspend fun getMyReviewList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<ReviewListResponse>>

    @GET("reviews/me/written")
    suspend fun getMyWrittenReviews(
        @Query("rating") rating: Int? = null,
        @Query("createdAt") createdAt: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<WrittenReviewListResponse>>
}