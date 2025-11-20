package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.ProfileApiService
import com.ssafy.yeobaeg.data.model.profile.UpdateProfileRequest
import com.ssafy.yeobaeg.data.model.profile.toDomain
import com.ssafy.yeobaeg.data.model.review.toDomain
import com.ssafy.yeobaeg.domain.model.profile.Artwork
import com.ssafy.yeobaeg.domain.model.profile.BookmarkCafe
import com.ssafy.yeobaeg.domain.model.profile.Profile
import com.ssafy.yeobaeg.domain.model.review.ReviewList
import com.ssafy.yeobaeg.domain.model.review.ReviewStatistics
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import javax.inject.Inject

private const val TAG = "ProfileRepositoryImpl"

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApiService
) : ProfileRepository {

    override suspend fun getProfile(): Result<Profile> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                val profileDto = response.body()?.data
                Log.d(TAG, "getProfile: $profileDto")
                if (profileDto != null) {
                    Result.success(profileDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get profile"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getProfile() error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getSomeoneProfile(userId: Long): Result<Profile> {
        return try {
            val response = api.getSomeoneProfile(userId)
            if (response.isSuccessful) {
                val profileDto = response.body()?.data
                Log.d(TAG, "getSomeoneProfile: $profileDto")
                if (profileDto != null) {
                    Result.success(profileDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get someone profile"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getSomeoneProfile() error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getBookmarkArtworkList(page: Int, size: Int): Result<Artwork> {
        return try {
            val response = api.getBookmarkArtworkList(page = page, size = size)
            if (response.isSuccessful) {
                val bookmarkDto = response.body()?.data
                Log.d(TAG, "getBookmarkArtworksList: $bookmarkDto")
                if (bookmarkDto != null) {
                    Result.success(bookmarkDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get bookmark artworks"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getBookmarkArtworksList() error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getBookmarkCafeList(page: Int, size: Int): Result<BookmarkCafe> {
        return try {
            val response = api.getBookmarkCafeList(page = page, size = size)
            if (response.isSuccessful) {
                val bookmarkDto = response.body()?.data
                if (bookmarkDto != null) {
                    Result.success(bookmarkDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get bookmark cafes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ProfileRepositoryImpl.kt
    override suspend fun updateProfile(
        nickname: String?,
        profileImageUrl: String?,
        introduction: String?,
    ): Result<Unit> {
        return runCatching {
            val request =
                UpdateProfileRequest(
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    introduction = introduction
                )

            val response = api.updateProfile(request)
            if (!response.isSuccessful) throw Exception("프로필 수정 실패: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun getMyReviewStatistics(): Result<ReviewStatistics> {
        return try {
            val response = api.getMyReviewStatistics()
            if (response.isSuccessful) {
                val statisticsDto = response.body()?.data
                Log.d(TAG, "getMyReviewStatistics: $statisticsDto")
                if (statisticsDto != null) {
                    Result.success(statisticsDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get review statistics"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMyReviewStatistics() error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getMyReviewList(page: Int, size: Int): Result<ReviewList> {
        return try {
            val response = api.getMyReviewList(page = page, size = size)
            if (response.isSuccessful) {
                val reviewListDto = response.body()?.data
                Log.d(TAG, "getMyReviewList: $reviewListDto")
                if (reviewListDto != null) {
                    Result.success(reviewListDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get review list"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMyReviewList() error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getMyWrittenReviews(
        rating: Int?,
        createdAt: String?,
        page: Int,
        size: Int
    ): Result<ReviewList> {
        return try {
            val response = api.getMyWrittenReviews(
                rating = rating,
                createdAt = createdAt,
                page = page,
                size = size
            )
            if (response.isSuccessful) {
                val writtenReviewListDto = response.body()?.data
                Log.d(TAG, "getMyWrittenReviews: $writtenReviewListDto")
                if (writtenReviewListDto != null) {
                    Result.success(writtenReviewListDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get written review list"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMyWrittenReviews() error: ${e.message}", e)
            Result.failure(e)
        }
    }

}