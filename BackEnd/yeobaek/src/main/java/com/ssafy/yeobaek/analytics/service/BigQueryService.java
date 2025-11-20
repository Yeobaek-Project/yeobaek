package com.ssafy.yeobaek.analytics.service;

import com.google.cloud.bigquery.TableResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * BigQuery를 통한 Firebase Analytics 데이터 조회 서비스
 */
public interface BigQueryService {

    /**
     * 특정 기간의 활성 사용자 수 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 날짜별 활성 사용자 수 맵
     */
    Map<String, Long> getActiveUsersByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 특정 기간의 이벤트 통계 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 이벤트명별 발생 횟수 맵
     */
    Map<String, Long> getEventCountsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 커스텀 BigQuery 쿼리 실행
     * @param query SQL 쿼리
     * @return 쿼리 결과
     */
    TableResult executeQuery(String query);

    /**
     * 쿼리 결과를 Map 리스트로 변환
     * @param result BigQuery 쿼리 결과
     * @return Map 형태의 결과 리스트
     */
    List<Map<String, Object>> convertToMapList(TableResult result);
}
