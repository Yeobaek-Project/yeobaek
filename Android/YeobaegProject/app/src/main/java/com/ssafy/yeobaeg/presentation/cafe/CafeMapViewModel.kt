package com.ssafy.yeobaeg.presentation.cafe

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.ssafy.yeobaeg.data.service.LocationService
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CafeMapUiState(
    val cafes: List<CafeSummary> = emptyList(),
    val isInitialLoading: Boolean = true,
    val error: String? = null,
    val currentLocation: LatLng? = null,
    val cameraPosition: LatLng? = null,
    val searchKeyword: String = "",
    val filter: CafeFilter = CafeFilter(),
    val isFilterDialogVisible: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val userRole: UserRole? = null,
    val hasLocationPermission: Boolean = false,
    val currentZoomLevel: Float = 15f,
    val selectedCafeId: Long? = null,
    val isFirstLoad: Boolean = true,  // 첫 로드 여부
    val shouldAnimateCamera: Boolean = false  // 카메라 애니메이션 트리거
)

@HiltViewModel
class CafeMapViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val locationService: LocationService,
    private val authSessionManager: AuthSessionManager,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(CafeMapUiState())
    val uiState: StateFlow<CafeMapUiState> = _uiState.asStateFlow()

    private var cameraMovementJob: Job? = null
    private val CAMERA_MOVEMENT_DEBOUNCE_MS = 1500L // 1.5초로 증가
    private var lastCameraPosition: LatLng? = null
    private val MIN_DISTANCE_FOR_API_CALL = 800.0 // 800m로 증가

    init { observeUserRole() }

    private fun observeUserRole() {
        viewModelScope.launch {
            authSessionManager.currentUserRole.collect { role ->
                _uiState.update { it.copy(userRole = role) }
            }
        }
    }

    fun updateLocationPermission(hasPermission: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
        // 첫 로드일 때만 현재 위치로 이동
        if (_uiState.value.isFirstLoad) {
            if (hasPermission) {
                loadCurrentLocation()
            } else {
                val defaultLocation = locationService.getDefaultLocation()
                val defaultLatLng = LatLng(defaultLocation.latitude, defaultLocation.longitude)
                _uiState.update { it.copy(currentLocation = defaultLatLng, cameraPosition = defaultLatLng, isFirstLoad = false) }
                loadCafesWithCurrentState()
            }
        }
    }

    fun loadCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationService.getCurrentLocation()
                    ?: locationService.getDefaultLocation()
                val latLng = LatLng(location.latitude, location.longitude)
                _uiState.update {
                    it.copy(
                        currentLocation = latLng,
                        cameraPosition = latLng,
                        isFirstLoad = false,
                        shouldAnimateCamera = true
                    )
                }
                loadCafesWithCurrentState()
            } catch (e: Exception) {
                Log.e("CafeMapViewModel", "Failed to get current location", e)
                _uiState.update { it.copy(error = "위치를 가져올 수 없습니다") }
            }
        }
    }

    /**
     * ★ 카메라 이동 종료 시 호출. reason으로 사용자 제스처만 허용
     */
    fun onCameraPositionChanged(position: LatLng, zoomLevel: Float, reason: CameraMoveStartedReason) {
        _uiState.update { it.copy(cameraPosition = position, currentZoomLevel = zoomLevel) }

        // 프로그램적 이동(API/DEVELOPER)은 무시 → InfoWindow 닫히는 원인 차단
        val isGesture = reason == CameraMoveStartedReason.GESTURE
        if (!isGesture) return

        cameraMovementJob?.cancel()
        cameraMovementJob = viewModelScope.launch {
            delay(CAMERA_MOVEMENT_DEBOUNCE_MS)

            val shouldCallApi = lastCameraPosition?.let { lastPos ->
                val distance = FloatArray(1)
                Location.distanceBetween(
                    lastPos.latitude, lastPos.longitude,
                    position.latitude, position.longitude,
                    distance
                )
                distance[0] >= MIN_DISTANCE_FOR_API_CALL
            } ?: true

            if (shouldCallApi) {
                loadCafesWithCurrentState(showLoading = false)
            }
        }
    }

    private fun calculateRadiusFromZoom(zoomLevel: Float): Double = when {
        zoomLevel >= 17f -> 1.0
        zoomLevel >= 15f -> 3.0
        zoomLevel >= 13f -> 5.0
        zoomLevel >= 11f -> 10.0
        zoomLevel >= 9f  -> 20.0
        else -> 50.0
    }

    private fun loadCafesWithCurrentState(showLoading: Boolean = true) {
        val state = _uiState.value
        val cameraPos = state.cameraPosition ?: return
        val myLocation = state.currentLocation ?: return

        val radiusKm = if (state.searchKeyword.isNotEmpty()) {
            100.0  // 검색어가 있으면 100km 범위로 검색
        } else {
            calculateRadiusFromZoom(state.currentZoomLevel)
        }
        lastCameraPosition = cameraPos

        loadCafes(
            centerLatitude = cameraPos.latitude,
            centerLongitude = cameraPos.longitude,
            myLatitude = myLocation.latitude,
            myLongitude = myLocation.longitude,
            radiusKm = radiusKm,
            keyword = state.searchKeyword.takeIf { it.isNotEmpty() },
            filter = state.filter.takeIf { it.isActive() },
            showLoading = showLoading
        )
    }

    private fun loadCafes(
        centerLatitude: Double,
        centerLongitude: Double,
        myLatitude: Double,
        myLongitude: Double,
        radiusKm: Double,
        keyword: String? = null,
        filter: CafeFilter? = null,
        showLoading: Boolean = true
    ) {
        viewModelScope.launch {
            if (showLoading) _uiState.update { it.copy(isInitialLoading = true, error = null) }

            cafeRepository.getCafes(
                keyword = keyword,
                centerLatitude = centerLatitude,
                centerLongitude = centerLongitude,
                myLatitude = myLatitude,
                myLongitude = myLongitude,
                radiusKm = radiusKm,
                filter = filter,
                page = 0,
                size = 50
            ).onSuccess { result ->
                _uiState.update { it.copy(cafes = result.cafes, isInitialLoading = false) }
            }.onFailure { e ->
                Log.e("CafeMapViewModel", "Failed to load cafes", e)
                _uiState.update { it.copy(isInitialLoading = false, error = e.message ?: "카페 목록을 불러올 수 없습니다") }
            }
        }
    }

    fun onSearchKeywordChanged(keyword: String) {
        _uiState.update { it.copy(searchKeyword = keyword) }
    }

    fun onSearch() {
        loadCafesWithCurrentState()
    }

    fun toggleFilterDialog() {
        _uiState.update { it.copy(isFilterDialogVisible = !it.isFilterDialogVisible) }
    }

    fun applyFilter(filter: CafeFilter) {
        _uiState.update { it.copy(filter = filter, isFilterDialogVisible = false) }
        loadCafesWithCurrentState()
    }

    fun clearFilter() {
        _uiState.update { it.copy(filter = CafeFilter()) }
        loadCafesWithCurrentState()
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    fun onCafeListItemClick(cafeId: Long) {
        val cafe = _uiState.value.cafes.find { it.cafeId == cafeId }
        val lat = cafe?.latitude
        val lng = cafe?.longitude

        if (lat != null && lng != null) {
            val targetPosition = LatLng(lat, lng)
            _uiState.update {
                it.copy(
                    cameraPosition = targetPosition,
                    currentZoomLevel = 17f,  // 상세 줌 레벨로 이동
                    selectedCafeId = cafeId,  // 선택된 카페 ID 설정
                    shouldAnimateCamera = true
                )
            }
        }
    }

    fun onCameraAnimationCompleted() {
        _uiState.update { it.copy(shouldAnimateCamera = false) }
    }

    fun clearSelectedCafe() {
        _uiState.update  { it.copy(selectedCafeId = null) }
    }

    fun navigateToCafeDetail(cafeId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.CafeDetail(cafeId = cafeId))
    }
}
