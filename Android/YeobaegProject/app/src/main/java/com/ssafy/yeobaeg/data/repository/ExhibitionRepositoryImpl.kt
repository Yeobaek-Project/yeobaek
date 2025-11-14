package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.ExhibitionApiService
import com.ssafy.yeobaeg.data.model.exhibition.request.toDto
import com.ssafy.yeobaeg.data.model.exhibition.response.toDomain
import com.ssafy.yeobaeg.data.model.exhibition.toDomain
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionInfo
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleArtworks
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleSlots
import com.ssafy.yeobaeg.domain.model.exhibition.ProposeExhibition
import com.ssafy.yeobaeg.domain.repository.ExhibitionRepository
import javax.inject.Inject

class ExhibitionRepositoryImpl @Inject constructor(
    private val exhibitionApiService: ExhibitionApiService
) : ExhibitionRepository {

    override suspend fun getSlotSchedule(
        slotId: Long,
        from: String
    ): Result<List<ExhibitionSchedule>> {
        return runCatching {
            val response = exhibitionApiService.getSlotSchedule(slotId, from)
            response.data?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun proposeExhibition(info: ProposeExhibition) {
        runCatching { exhibitionApiService.proposeExhibition(info.toDto()) }
    }

    override suspend fun getExhibitionInfo(exhibitionId: Long): Result<ExhibitionInfo> {
        return runCatching {
            val response = exhibitionApiService.getExhibitionInfo(exhibitionId)
            response.data?.toDomain() ?: throw IllegalStateException("데이터가 존재하지 않습니다.")
        }
    }

    override suspend fun agreeExhibitionPropose(exhibitionId: Long) {
        runCatching { exhibitionApiService.agreeExhibitionPropose(exhibitionId) }
    }

    override suspend fun getPossibleArtworks(
        artistId: Long,
        startDate: String,
        endDate: String
    ): Result<PossibleArtworks> {
        return runCatching {
            val response = exhibitionApiService.getPossibleArtworks(
                artistId = artistId,
                startDate = startDate,
                endDate = endDate
            )
            response.data?.toDomain() ?: throw IllegalStateException("데이터가 존재하지 않습니다.")
        }
    }

    override suspend fun getPossibleSlots(
        cafeId: Long,
        startDate: String,
        endDate: String
    ): Result<PossibleSlots> {
        return runCatching {
            val response = exhibitionApiService.getPossibleSlots(
                cafeId = cafeId,
                startDate = startDate,
                endDate = endDate
            )
            response.data?.toDomain() ?: throw IllegalStateException("데이터가 존재하지 않습니다.")
        }
    }

}
