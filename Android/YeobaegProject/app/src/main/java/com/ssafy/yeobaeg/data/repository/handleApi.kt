// CafeRepositoryImpl.kt 파일 상단 또는 유틸리티 파일에 추가
import com.ssafy.yeobaeg.data.model.BaseResponse
import kotlinx.serialization.json.Json
import retrofit2.HttpException

/**
 * API 호출 결과를 처리하여 성공 시 데이터를 반환하고, 모든 종류의 실패 시 Exception을 던진다.
 * 이 함수가 던진 Exception은 runCatching 블록이 잡아 Result.failure로 변환한다.
 *
 * @param T API 응답의 data 타입
 * @param json 에러 본문 파싱을 위한 Json 객체
 * @param execute 실제 Retrofit API 호출 람다
 * @return 성공 시 API 응답의 data 객체 (T)
 */
suspend fun <T> handleApi(
    json: Json,
    execute: suspend () -> BaseResponse<T>
): T {
    try {
        val response = execute()

        if (response.code.startsWith("S")) {
            // 성공
            // data가 null인 경우는 Unit 타입일 때만 허용된다고 가정
            @Suppress("UNCHECKED_CAST")
            return response.data ?: Unit as T
        } else {
            // API 호출은 성공했으나, 서버가 비즈니스 로직 에러를 반환
            throw Exception(response.message ?: "알 수 없는 오류가 발생했습니다. (코드: ${response.code})")
        }
    } catch (e: HttpException) {
        // HTTP 상태 코드 에러 (4xx, 5xx 등)
        val errorBody = e.response()?.errorBody()?.string()
        val errorMessage = if (!errorBody.isNullOrBlank()) {
            try {
                // 서버가 보내준 JSON 에러 메시지 파싱
                val errorResponse = json.decodeFromString<BaseResponse<Unit?>>(errorBody)
                errorResponse.message ?: "오류가 발생했습니다."
            } catch (e: Exception) {
                errorBody // 파싱 실패 시 원본 텍스트 반환
            }
        } else {
            "네트워크 오류가 발생했습니다. (코드: ${e.code()})"
        }
        throw Exception(errorMessage)
    } catch (e: Exception) {
        // 그 외 모든 예외 (네트워크 연결 불가 등)
        throw e
    }
}