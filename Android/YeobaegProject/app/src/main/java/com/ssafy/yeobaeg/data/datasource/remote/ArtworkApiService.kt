package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.art.request.ArtworkRegisterRequest
import com.ssafy.yeobaeg.data.model.profile.ArtworksResponse
import com.ssafy.yeobaeg.data.model.response.ArtHomeResponse
import com.ssafy.yeobaeg.data.model.response.ArtSearchResponse
import com.ssafy.yeobaeg.data.model.response.ArtworkDetailResponse
import com.ssafy.yeobaeg.data.model.response.PopularArtworksData
import com.ssafy.yeobaeg.data.model.response.ToggleBookmarkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtworkApiService {

    @GET("artworks")
    suspend fun getArtworks(
        @Query("keyword") keyword: String? = null,
        @Query("categories") categories: String? = null,
        @Query("minWidth") minWidth: Int? = null,
        @Query("maxWidth") maxWidth: Int? = null,
        @Query("minHeight") minHeight: Int? = null,
        @Query("maxHeight") maxHeight: Int? = null,
        @Query("sortBy") sortBy: String = "LATEST",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): BaseResponse<ArtSearchResponse>


    @GET("artworks/{artworkId}")
    suspend fun getArtworkDetail(
        @Path("artworkId") artworkId: Long
    ): BaseResponse<ArtworkDetailResponse>

    @POST("artworks/{artworkId}/bookmarks")
    suspend fun toggleBookmark(
        @Path("artworkId") artworkId: Long
    ): BaseResponse<ToggleBookmarkResponse>

    @GET("artworks/popular")
    suspend fun getPopularArtworks(
        @Query("size") size: Int = 10
    ): BaseResponse<PopularArtworksData>

    @POST("artworks")
    suspend fun registerArtwork(
        @Body request: ArtworkRegisterRequest
    ) : BaseResponse<String>

    @GET("artworks/me/home")
    suspend fun getMyArtHome(): BaseResponse<ArtHomeResponse>

    @GET("artworks/me")
    suspend fun getMyArtworks(
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): BaseResponse<ArtworksResponse>

    @GET("artworks/users/{userId}")
    suspend fun getUserArtworks(
        @Path("userId") userId: Long,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): BaseResponse<ArtworksResponse>

    // TODO: api 바뀌면 쿼리명 수정
    @GET("artworks/recommendations")
    suspend fun getRecommendedArtworks(
        @Query("cafeId") slotId: Long,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): BaseResponse<ArtworksResponse>
}

