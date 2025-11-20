package com.ssafy.yeobaek.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Firebase Admin SDK 초기화 설정
 * 애플리케이션 시작 시 Firebase를 초기화하여 BigQuery 연동 등에 사용
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-file}")
    private Resource serviceAccountFile;

    @Value("${firebase.project-id}")
    private String projectId;

    /**
     * Firebase Admin SDK 초기화
     * 서비스 계정 JSON 파일을 로드하여 FirebaseApp 인스턴스 생성
     */
    @PostConstruct
    public void initialize() {
        try {
            // 이미 초기화된 경우 스킵
            if (!FirebaseApp.getApps().isEmpty()) {
                log.info("Firebase App이 이미 초기화되어 있습니다.");
                return;
            }

            // 서비스 계정 파일에서 인증 정보 로드
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    serviceAccountFile.getInputStream()
            );

            // Firebase 옵션 설정
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            // Firebase 초기화
            FirebaseApp.initializeApp(options);

            log.info("Firebase Admin SDK 초기화 완료 - Project ID: {}", projectId);

        } catch (IOException e) {
            log.error("Firebase 초기화 실패: 서비스 계정 파일을 찾을 수 없습니다. - {}", e.getMessage());
            throw new IllegalStateException("Firebase 초기화 실패", e);
        } catch (Exception e) {
            log.error("Firebase 초기화 중 오류 발생 - {}", e.getMessage());
            throw new IllegalStateException("Firebase 초기화 실패", e);
        }
    }
}
