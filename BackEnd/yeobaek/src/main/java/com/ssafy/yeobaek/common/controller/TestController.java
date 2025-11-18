package com.ssafy.yeobaek.common.controller;

import com.ssafy.yeobaek.cafe.dto.response.Coordinates;
import com.ssafy.yeobaek.cafe.service.GeocoderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Tag(name = "테스트 컨트롤러", description = "기능 테스트용 API")
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final GeocoderService geocoderService;

    @PersistenceContext
    private EntityManager entityManager;

    @Operation(summary = "지오코딩 테스트", description = "주소를 좌표로 변환하는 기능을 테스트합니다.")
    @GetMapping("/geocode")
    public ResponseEntity<Coordinates> geocodeAddress(@RequestParam String address) {
        Coordinates coordinates = geocoderService.getCoordinates(address);
        if (coordinates != null) {
            return ResponseEntity.ok(coordinates);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "데이터베이스 초기화", description = "모든 테이블의 데이터를 삭제하고 초기 데이터를 다시 삽입합니다.")
    @PostMapping("/database/reset")
    @Transactional
    public ResponseEntity<String> resetDatabase() {
        try {
            // Truncate
            log.info("Starting database truncation...");
            String truncateSql = readSqlFile("truncate.sql");
            executeSqlScript(truncateSql);
            log.info("Database truncation finished.");

            // Insert
            log.info("Starting data insertion...");
            String insertSql = readSqlFile("insert.sql");
            executeSqlScript(insertSql);
            log.info("Data insertion finished.");

            return ResponseEntity.ok("Database has been reset.");
        } catch (IOException e) {
            log.error("Could not read SQL files", e);
            return ResponseEntity.internalServerError().body("Could not read SQL files: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error resetting database", e);
            return ResponseEntity.internalServerError().body("Error resetting database: " + e.getMessage());
        }
    }

    private void executeSqlScript(String sqlScript) {
        // Remove all comment lines
        String cleanedSql = sqlScript.replaceAll("(?m)^--.*$", "");
        
        String[] statements = cleanedSql.split(";");

        for (String s : statements) {
            String trimmedStatement = s.trim();
            if (!trimmedStatement.isEmpty()) {
                log.info("Executing SQL statement: {}", trimmedStatement);
                entityManager.createNativeQuery(trimmedStatement).executeUpdate();
            }
        }
    }

    private String readSqlFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}

