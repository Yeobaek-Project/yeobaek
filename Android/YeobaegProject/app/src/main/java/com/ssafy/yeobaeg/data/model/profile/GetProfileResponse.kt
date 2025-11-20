package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.domain.model.profile.ArtistInfo
import com.ssafy.yeobaeg.domain.model.profile.CafeInfo
import com.ssafy.yeobaeg.domain.model.profile.Profile
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.profile.ProfileRatingSummary
import kotlinx.serialization.Serializable

@Serializable
data class GetProfileResponseDto(
    val user: UserProfileResponse,
    val cafe: CafeProfileResponse?=null,
    val artist: ArtistProfileResponse?=null
)

// DTO → Domain 변환
fun GetProfileResponseDto.toDomain(): Profile = Profile(
    userInfo = user.toDomain(),
    cafeInfo = cafe?.toDomain(),
    artistInfo = artist?.toDomain()
)

fun UserProfileResponse.toDomain(): UserInfo = UserInfo(
    userId = userId,
    email = email,
    nickname = nickname,
    userType = UserRole.valueOf(userType),
    profileImageUrl = profileImageUrl,
    birthDate = birthDate,
    gender = gender
)

fun CafeProfileResponse.toDomain(): CafeInfo = CafeInfo(
    cafeId = cafeId,
    cafeName = cafeName,
    address = address,
    description = description,
    contact = contact,
    businessHoursStart=businessHoursStart,
    businessHoursEnd=businessHoursEnd,
    ratingStats = ratingStats.toDomain()
)

fun ArtistProfileResponse.toDomain(): ArtistInfo = ArtistInfo(
    introduction = introduction ?: "",
    artworkCount = artworkCount,
    exhibitingCount = exhibitingCount,
    ratingStats = ratingStats.toDomain()
)

fun ProfileRatingSummaryResponse.toDomain(): ProfileRatingSummary = ProfileRatingSummary(
    totalReviews = totalReviews,
    averageRating = averageRating
)