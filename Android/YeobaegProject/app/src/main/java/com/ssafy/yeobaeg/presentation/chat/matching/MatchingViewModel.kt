package com.ssafy.yeobaeg.presentation.chat.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleArtworkSummary
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleSlotSummary
import com.ssafy.yeobaeg.domain.model.exhibition.ProposeExhibition
import com.ssafy.yeobaeg.domain.repository.ExhibitionRepository
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI 상태 정의
data class MatchingUiState(
    val proposal: ProposeExhibition = ProposeExhibition(),
    val possibleSlots: List<PossibleSlotSummary> = emptyList(),
    val possibleArtworks: List<PossibleArtworkSummary> = emptyList(),
    val myRole: UserRole = UserRole.ARTIST,
    val myId: Long = 0L,
    val partnerId: Long? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val step:Int=1,
    val errorMessage: String? = null
)


@HiltViewModel
class MatchingViewModel @Inject constructor(
    private val exhibitionRepository: ExhibitionRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchingUiState())
    val uiState: StateFlow<MatchingUiState> = _uiState.asStateFlow()

    // 날짜 선택
    fun setDate(start: String, end: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(proposal = it.proposal.copy(startDate = start, endDate = end), step = 2)
        }
        navigator.navigate(FullScreen.MatchingSpaceScreen)
    }


    fun setSpace(spaceId: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(proposal = it.proposal.copy(slotId = spaceId), step = 3)
        }
        navigator.navigate(FullScreen.MatchingArtworkScreen)
    }

    fun setArtwork(artworkId: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(proposal = it.proposal.copy(artworkId = artworkId), step = 4)
        }
        navigator.navigate(FullScreen.MatchingFeeScreen)
    }

    fun setCommission(fee: Double) = viewModelScope.launch {
        _uiState.update {
            it.copy(proposal = it.proposal.copy(commissionRate = fee), step = 5)
        }
        navigator.navigate(FullScreen.MatchingNoteScreen)
    }


    // 요구사항 입력
    fun setRequirements(note: String) {
        _uiState.value = _uiState.value.copy(
            proposal = _uiState.value.proposal.copy(
                requirements = note
            )
        )
    }

    //사용 가능한 슬롯 리스트
    fun loadPossibleSlots() {
        val role = _uiState.value.myRole
        val startDate = _uiState.value.proposal.startDate
        val endDate = _uiState.value.proposal.endDate

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            if (role == UserRole.CAFE_OWNER) {
                // 내가 카페 사장이면 내 카페 슬롯 조회
                val myId = _uiState.value.myId
                exhibitionRepository.getPossibleSlots(myId, startDate, endDate)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            possibleSlots = it.slots,
                            isLoading = false
                        )
                    }
                    .onFailure { e ->
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, errorMessage = e.message)
                    }
            } else {
                // 내가 작가라면 상대방의 아이디로 슬롯 조회
                val partnerId = _uiState.value.partnerId ?: return@launch
                exhibitionRepository.getPossibleSlots(partnerId, startDate, endDate)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            possibleSlots = it.slots,
                            isLoading = false
                        )
                    }
                    .onFailure { e ->
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, errorMessage = e.message)
                    }
            }
        }
    }

    //가능한 작품 리스트
    fun loadPossibleArtworks() {
        val role = _uiState.value.myRole
        val startDate = _uiState.value.proposal.startDate
        val endDate = _uiState.value.proposal.endDate

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            if (role == UserRole.ARTIST) {
                // 내가 작가일 때 → 내 작품
                val artistId = _uiState.value.myId
                exhibitionRepository.getPossibleArtworks(artistId, startDate, endDate)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            possibleArtworks = it.artworks,
                            isLoading = false
                        )
                    }
                    .onFailure { e ->
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, errorMessage = e.message)
                    }
            } else {
                // 내가 카페 사장일 때 → 상대방(작가)의 작품
                val partnerArtistId = _uiState.value.partnerId ?: return@launch
                exhibitionRepository.getPossibleArtworks(partnerArtistId, startDate, endDate)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            possibleArtworks = it.artworks,
                            isLoading = false
                        )
                    }
                    .onFailure { e ->
                        _uiState.value =
                            _uiState.value.copy(isLoading = false, errorMessage = e.message)
                    }
            }
        }
    }


    // 매칭 요청 API
    fun requestMatching() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val myRole = _uiState.value.myRole
            val myId = _uiState.value.myId
            val partnerId = _uiState.value.partnerId ?: 0L

            val finalProposal = if (myRole == UserRole.CAFE_OWNER) {
                // 내가 카페 사장이면
                _uiState.value.proposal.copy(
                    cafeId = myId,
                )
            } else {
                // 내가 작가면
                _uiState.value.proposal.copy(
                    cafeId = partnerId,
                )
            }


            runCatching {
                exhibitionRepository.proposeExhibition(finalProposal)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
                navigator.navigateBack(FullScreen.ChattingScreen(0),true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun setChatContext(myRole: UserRole?, partnerId: Long) {
        _uiState.value = _uiState.value.copy(
            myRole = myRole?: UserRole.GENERAL,
            partnerId = partnerId
        )
    }

    fun setMyId(id: Long?) {
        _uiState.value = _uiState.value.copy(myId = id?:0L)
    }

}
