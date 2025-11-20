package com.ssafy.yeobaeg.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.review.ReviewDetail
import com.ssafy.yeobaeg.domain.model.review.ReviewStatistics
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewManagementUiState(
    val isLoading: Boolean = false,
    val statistics: ReviewStatistics? = null,
    val reviews: List<ReviewDetail> = emptyList(),
    val currentPage: Int = 0,
    val hasNext: Boolean = false,
    val writtenReviews: List<ReviewDetail> = emptyList(),
    val writtenCurrentPage: Int = 0,
    val writtenHasNext: Boolean = false,
    val writtenIsLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ReviewManagementViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewManagementUiState())
    val uiState: StateFlow<ReviewManagementUiState> = _uiState.asStateFlow()

    init {
        loadReviewStatistics()
        loadReviewList()
        loadWrittenReviewList()
    }

    private fun loadReviewStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getMyReviewStatistics()
                .onSuccess { statistics ->
                    _uiState.value = _uiState.value.copy(
                        statistics = statistics,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    private fun loadReviewList(page: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getMyReviewList(page = page, size = 20)
                .onSuccess { reviewList ->
                    _uiState.value = _uiState.value.copy(
                        reviews = if (page == 0) reviewList.reviews else _uiState.value.reviews + reviewList.reviews,
                        currentPage = reviewList.pagination.currentPage,
                        hasNext = reviewList.pagination.hasNext,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun loadNextPage() {
        if (!_uiState.value.isLoading && _uiState.value.hasNext) {
            loadReviewList(_uiState.value.currentPage + 1)
        }
    }

    private fun loadWrittenReviewList(page: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(writtenIsLoading = true)
            repository.getMyWrittenReviews(
                rating = null,
                createdAt = null,
                page = page,
                size = 20
            )
                .onSuccess { writtenReviewList ->
                    _uiState.value = _uiState.value.copy(
                        writtenReviews = if (page == 0) writtenReviewList.reviews else _uiState.value.writtenReviews + writtenReviewList.reviews,
                        writtenCurrentPage = writtenReviewList.pagination.currentPage,
                        writtenHasNext = writtenReviewList.pagination.hasNext,
                        writtenIsLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        writtenIsLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun loadWrittenNextPage() {
        if (!_uiState.value.writtenIsLoading && _uiState.value.writtenHasNext) {
            loadWrittenReviewList(_uiState.value.writtenCurrentPage + 1)
        }
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

}