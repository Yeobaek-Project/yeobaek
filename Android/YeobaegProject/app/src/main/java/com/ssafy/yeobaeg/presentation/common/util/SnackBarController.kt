package com.ssafy.yeobaeg.presentation.common.util

import com.ssafy.yeobaeg.presentation.common.component.SnackbarType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 전역 스낵바 메시지
 */
data class SnackbarMessage(
    val message: String,
    val type: SnackbarType
)

/**
 * 전역 스낵바 컨트롤러
 *
 * 앱의 어디서든 스낵바를 표시할 수 있도록 하는 클래스
 * MainActivity에서 구독하여 전역 스낵바로 표시
 * Singleton으로 관리되어 앱 전체에서 하나의 인스턴스 사용
 */
@Singleton
class SnackBarController @Inject constructor() {

    private val _snackbarEvents = MutableSharedFlow<SnackbarMessage>()

    /**
     * 스낵바 이벤트 스트림
     */
    val snackbarEvents: SharedFlow<SnackbarMessage> = _snackbarEvents.asSharedFlow()

    /**
     * 성공 스낵바 표시
     */
    suspend fun showSuccess(message: String) {
        _snackbarEvents.emit(
            SnackbarMessage(
                message = message,
                type = SnackbarType.SUCCESS
            )
        )
    }

    /**
     * 에러 스낵바 표시
     */
    suspend fun showError(message: String) {
        _snackbarEvents.emit(
            SnackbarMessage(
                message = message,
                type = SnackbarType.ERROR
            )
        )
    }

    /**
     * 정보 스낵바 표시
     */
    suspend fun showInfo(message: String) {
        _snackbarEvents.emit(
            SnackbarMessage(
                message = message,
                type = SnackbarType.INFO
            )
        )
    }
}
