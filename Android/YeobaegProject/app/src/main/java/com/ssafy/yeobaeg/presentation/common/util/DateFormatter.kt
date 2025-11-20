package com.ssafy.yeobaeg.presentation.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 날짜 포맷팅 유틸리티
 */
object DateFormatter {
    
    private const val API_DATE_FORMAT = "yyyy-MM-dd"
    
    /**
     * Date를 API 형식(YYYY-MM-DD)으로 포맷팅
     */
    fun formatToApiFormat(date: Date): String {
        val formatter = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Calendar를 API 형식(YYYY-MM-DD)으로 포맷팅
     */
    fun formatToApiFormat(calendar: Calendar): String {
        return formatToApiFormat(calendar.time)
    }
    
    /**
     * 년, 월, 일을 API 형식(YYYY-MM-DD)으로 포맷팅
     */
    fun formatToApiFormat(year: Int, month: Int, dayOfMonth: Int): String {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth)
    }
    
    /**
     * API 형식의 날짜 문자열을 Date로 파싱
     */
    fun parseFromApiFormat(dateString: String): Date? {
        return try {
            val formatter = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * API 형식의 날짜 문자열을 표시 형식으로 변환
     * 예: 1990-01-01 -> 1990년 1월 1일
     */
    fun toDisplayFormat(apiDateString: String): String? {
        return parseFromApiFormat(apiDateString)?.let { date ->
            val calendar = Calendar.getInstance().apply { time = date }
            "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월 ${calendar.get(Calendar.DAY_OF_MONTH)}일"
        }
    }

    /**
     * ISO 날짜 문자열(yyyy-MM-dd'T'HH:mm:ss)을 사용자가 보기 좋은 시간으로 변환
     * 예: "방금 전", "5분 전", "3시간 전", "어제", "2일 전"
     */
    fun toTimeAgo(dateString: String): String {
        if (dateString.isBlank()) return ""

        val date = try {
            // ISO 8601 포맷 (서버 응답 대부분)
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            try {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateString)
            } catch (e2: Exception) {
                try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                } catch (e3: Exception) {
                    null
                }
            }
        } ?: return ""

        val now = Date()
        val diffMillis = now.time - date.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days < 7 -> "${days}일 전"
            else -> {
                val formatter = SimpleDateFormat("M월 d일", Locale.getDefault())
                formatter.format(date)
            }
        }
    }



}

