package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.exhibition.ExhibitionScheduleDto
import com.ssafy.yeobaeg.data.model.exhibition.request.ProposeExhibitionRequest
import com.ssafy.yeobaeg.data.model.exhibition.response.GetExhibitionInfoResponse
import com.ssafy.yeobaeg.data.model.exhibition.response.GetPossibleArtworksResponse
import com.ssafy.yeobaeg.data.model.exhibition.response.GetPossibleSlotsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExhibitionApiService {

    /**
     * 슬롯의 전시 일정 조회
     *
     * @param slotId 슬롯 ID
     * @param from 시작 날짜 (yyyy-MM-dd 형식, 기본값: 오늘)
     * @return 전시 일정 목록 (30일간)
     */
    @GET("exhibitions/slots/{slotId}")
    suspend fun getSlotSchedule(
        @Path("slotId") slotId: Long,
        @Query("from") from: String
    ): BaseResponse<List<ExhibitionScheduleDto>>

    @POST("exhibitions")
    suspend fun proposeExhibition(
        @Body request: ProposeExhibitionRequest
    ): BaseResponse<Unit>

    @GET("exhibitions/{exhibitionId}")
    suspend fun getExhibitionInfo(
        @Path("exhibitionId") exhibitionId: Long
    ): BaseResponse<GetExhibitionInfoResponse>

    @POST("exhibition/{exhibitionId}/agree")
    suspend fun agreeExhibitionPropose(
        @Path("exhibitionId") exhibitionId: Long
    ): BaseResponse<Unit>

    @GET("artworks/available-artworks")
    suspend fun getPossibleArtworks(
        @Query("artistId") artistId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): BaseResponse<GetPossibleArtworksResponse>

    @GET("cafes/{cafeId}/available-slots")
    suspend fun getPossibleSlots(
        @Path("cafeId") cafeId:Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): BaseResponse<GetPossibleSlotsResponse>
}
