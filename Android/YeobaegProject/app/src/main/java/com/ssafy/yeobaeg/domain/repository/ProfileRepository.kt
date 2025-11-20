package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.profile.Artwork
import com.ssafy.yeobaeg.domain.model.profile.BookmarkCafe
import com.ssafy.yeobaeg.domain.model.profile.Profile
import com.ssafy.yeobaeg.domain.model.review.ReviewList
import com.ssafy.yeobaeg.domain.model.review.ReviewStatistics

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun getSomeoneProfile(userId:Long): Result<Profile>
    suspend fun getBookmarkArtworkList(page: Int, size: Int): Result<Artwork>
    suspend fun getBookmarkCafeList(page: Int, size: Int): Result<BookmarkCafe>
    suspend fun updateProfile(
        nickname: String?,
        profileImageUrl: String?,
        introduction: String?,
    ):Result<Unit>
    suspend fun getMyReviewStatistics(): Result<ReviewStatistics>
    suspend fun getMyReviewList(page: Int, size: Int): Result<ReviewList>
    suspend fun getMyWrittenReviews(
        rating: Int? = null,
        createdAt: String? = null,
        page: Int,
        size: Int
    ): Result<ReviewList>
}

