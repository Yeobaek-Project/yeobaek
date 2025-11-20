package com.ssafy.yeobaek.analytics.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BigQuery를 통한 Firebase Analytics 데이터 조회 서비스 구현체
 */
@Slf4j
@Service
public class BigQueryServiceImpl implements BigQueryService {

    private final BigQuery bigQuery;
    private final String projectId;
    private final String datasetId;

    public BigQueryServiceImpl(
            @Value("${firebase.service-account-file}") Resource serviceAccountFile,
            @Value("${google.cloud.bigquery.project-id}") String projectId,
            @Value("${google.cloud.bigquery.dataset-id}") String datasetId
    ) throws IOException {
        this.projectId = projectId;
        this.datasetId = datasetId;

        // BigQuery 클라이언트 초기화
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                serviceAccountFile.getInputStream()
        );

        this.bigQuery = BigQueryOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build()
                .getService();

        log.info("BigQuery 서비스 초기화 완료 - Project: {}, Dataset: {}", projectId, datasetId);
    }

    /**
     * 특정 기간의 활성 사용자 수 조회
     */
    @Override
    public Map<String, Long> getActiveUsersByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("활성 사용자 수 조회 - 기간: {} ~ {}", startDate, endDate);

        String query = String.format(
                """
                SELECT
                  event_date,
                  COUNT(DISTINCT user_pseudo_id) as active_users
                FROM
                  `%s.%s.events_*`
                WHERE
                  _TABLE_SUFFIX BETWEEN '%s' AND '%s'
                GROUP BY
                  event_date
                ORDER BY
                  event_date
                """,
                projectId,
                datasetId,
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        );

        try {
            TableResult result = executeQuery(query);
            Map<String, Long> userCounts = new HashMap<>();

            for (FieldValueList row : result.iterateAll()) {
                String date = row.get("event_date").getStringValue();
                Long count = row.get("active_users").getLongValue();
                userCounts.put(date, count);
            }

            log.info("활성 사용자 수 조회 완료 - 결과 개수: {}", userCounts.size());
            return userCounts;

        } catch (Exception e) {
            log.error("활성 사용자 수 조회 실패 - {}", e.getMessage());
            throw new RuntimeException("BigQuery 조회 실패", e);
        }
    }

    /**
     * 특정 기간의 이벤트 통계 조회
     */
    @Override
    public Map<String, Long> getEventCountsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("이벤트 통계 조회 - 기간: {} ~ {}", startDate, endDate);

        String query = String.format(
                """
                SELECT
                  event_name,
                  COUNT(*) as event_count
                FROM
                  `%s.%s.events_*`
                WHERE
                  _TABLE_SUFFIX BETWEEN '%s' AND '%s'
                GROUP BY
                  event_name
                ORDER BY
                  event_count DESC
                LIMIT 50
                """,
                projectId,
                datasetId,
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        );

        try {
            TableResult result = executeQuery(query);
            Map<String, Long> eventCounts = new HashMap<>();

            for (FieldValueList row : result.iterateAll()) {
                String eventName = row.get("event_name").getStringValue();
                Long count = row.get("event_count").getLongValue();
                eventCounts.put(eventName, count);
            }

            log.info("이벤트 통계 조회 완료 - 이벤트 종류: {}", eventCounts.size());
            return eventCounts;

        } catch (Exception e) {
            log.error("이벤트 통계 조회 실패 - {}", e.getMessage());
            throw new RuntimeException("BigQuery 조회 실패", e);
        }
    }

    /**
     * 커스텀 BigQuery 쿼리 실행
     */
    @Override
    public TableResult executeQuery(String query) {
        log.debug("BigQuery 쿼리 실행:\n{}", query);

        try {
            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                    .setUseLegacySql(false)
                    .build();

            TableResult result = bigQuery.query(queryConfig);
            log.debug("쿼리 실행 완료 - 결과 행 수: {}", result.getTotalRows());

            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("쿼리 실행 중단 - {}", e.getMessage());
            throw new RuntimeException("쿼리 실행 중단", e);
        } catch (Exception e) {
            log.error("쿼리 실행 실패 - {}", e.getMessage());
            throw new RuntimeException("쿼리 실행 실패", e);
        }
    }

    /**
     * 쿼리 결과를 Map 리스트로 변환
     */
    @Override
    public List<Map<String, Object>> convertToMapList(TableResult result) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (FieldValueList row : result.iterateAll()) {
            Map<String, Object> rowMap = new HashMap<>();

            for (Field field : result.getSchema().getFields()) {
                String fieldName = field.getName();
                FieldValue fieldValue = row.get(fieldName);

                // null 처리
                if (fieldValue.isNull()) {
                    rowMap.put(fieldName, null);
                    continue;
                }

                // 타입별 변환
                Object value = switch (field.getType().getStandardType()) {
                    case STRING -> fieldValue.getStringValue();
                    case INT64 -> fieldValue.getLongValue();
                    case FLOAT64 -> fieldValue.getDoubleValue();
                    case BOOL -> fieldValue.getBooleanValue();
                    case TIMESTAMP -> fieldValue.getTimestampValue();
                    default -> fieldValue.getValue();
                };

                rowMap.put(fieldName, value);
            }

            resultList.add(rowMap);
        }

        return resultList;
    }
}
