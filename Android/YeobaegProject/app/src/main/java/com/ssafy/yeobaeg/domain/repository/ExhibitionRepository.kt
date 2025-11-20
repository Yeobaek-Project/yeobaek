package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionInfo
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleArtworks
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleSlots
import com.ssafy.yeobaeg.domain.model.exhibition.ProposeExhibition

interface ExhibitionRepository {

    /**
     * 슬롯의 전시 일정 조회
     *
     * @param slotId 슬롯 ID
     * @param from 시작 날짜 (yyyy-MM-dd 형식)
     * @return 전시 일정 목록 (30일간)
     */
    suspend fun getSlotSchedule(
        slotId: Long,
        from: String
    ): Result<List<ExhibitionSchedule>>

    suspend fun proposeExhibition(info: ProposeExhibition)
    suspend fun getExhibitionInfo(exhibitionId: Long?): Result<ExhibitionInfo>
    suspend fun agreeExhibitionPropose(exhibitionId: Long): Result<Unit>
    suspend fun getPossibleArtworks(
        artistId: Long,
        startDate: String,
        endDate: String
    ): Result<PossibleArtworks>

    suspend fun getPossibleSlots(
        cafeId: Long,
        startDate: String,
        endDate: String
    ): Result<PossibleSlots>

    suspend fun rejectExhibition(exhibitionId: Long):Result<Unit>
}
