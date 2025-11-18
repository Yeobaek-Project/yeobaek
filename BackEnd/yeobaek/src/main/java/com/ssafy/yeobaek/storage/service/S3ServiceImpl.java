package com.ssafy.yeobaek.storage.service;

import com.ssafy.yeobaek.storage.dto.request.PresignedUrlRequest;
import com.ssafy.yeobaek.storage.dto.response.PresignedUrl;
import com.ssafy.yeobaek.storage.dto.response.PresignedUrlListData;
import com.ssafy.yeobaek.common.enums.StorageFolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private static final Long PRESIGNED_URL_EXPIRATION_MINUTES = 10L;

    /**
     * 다중 Presigned URL 쌍 생성 (업로드용 + 조회용)
     * @param request Presigned URL 생성 요청 정보 (저장 폴더, 개수)
     * @return Presigned URL 리스트 응답 (fileKey, uploadUrl, downloadUrl)
     */
    @Override
    public PresignedUrlListData generatePresignedUrlsForUpload(PresignedUrlRequest request) {
        log.info("업로드용 Presigned URL 다중 생성 시작 - 폴더: {}, 개수: {}", request.getFolder(), request.getCount());
        List<PresignedUrl> presignedUrls = new ArrayList<>();
        StorageFolder folder = StorageFolder.valueOf(request.getFolder());

        for (int i = 0; i < request.getCount(); i++) {
            String fileKey = generateFileKey(folder);
            presignedUrls.add(generatePresignedUrlPair(fileKey));
        }

        log.info("업로드용 Presigned URL {}개 생성 완료", presignedUrls.size());
        return new PresignedUrlListData(presignedUrls);
    }

    /**
     * 단일 Presigned URL 쌍 생성 (업로드용 + 조회용)
     * @param fileKey S3 파일 키
     * @return Presigned URL 쌍 (fileKey, uploadUrl, downloadUrl)
     */
    @Override
    public PresignedUrl generatePresignedUrlPair(String fileKey) {
        log.debug("Presigned URL 쌍 생성 시작 - 파일 키: {}", fileKey);
        String uploadUrl = generatePresignedUrlForUpload(fileKey);
        String downloadUrl = generatePresignedUrlForDownload(fileKey);
        PresignedUrl presignedUrl = PresignedUrl.of(fileKey, uploadUrl, downloadUrl);
        log.debug("Presigned URL 쌍 생성 완료 - 파일 키: {}", fileKey);
        return presignedUrl;
    }

    /**
     * 업로드용 Presigned URL 생성
     * @param fileKey S3 파일 키
     * @return 업로드용 Presigned URL 문자열
     */
    @Override
    public String generatePresignedUrlForUpload(String fileKey) {
        log.debug("업로드용 Presigned URL 생성 - 파일 키: {}", fileKey);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    /**
     * 조회용 Presigned URL 생성
     * fileKey가 null이면 null로 반환
     * @param fileKey S3 파일 키
     * @return 조회용 Presigned URL 문자열
     */
    @Override
    public String generatePresignedUrlForDownload(String fileKey) {
        if (fileKey == null) {
            return null;
        }
        log.debug("조회용 Presigned URL 생성 - 파일 키: {}", fileKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    /**
     * 여러 파일의 조회용 Presigned URL 생성
     * fileKeys가 null이면 빈 리스트 반환, null 요소는 null로 변환
     * @param fileKeys S3 파일 키 리스트
     * @return 조회용 Presigned URL 리스트
     */
    @Override
    public List<String> generatePresignedUrlsForDownload(List<String> fileKeys) {
        if (fileKeys == null) {
            return new ArrayList<>();
        }
        log.debug("조회용 Presigned URL 다중 생성 시작 - 파일 키 개수: {}", fileKeys.size());

        return fileKeys.stream()
                .map(this::generatePresignedUrlForDownload)
                .collect(Collectors.toList());
    }

    /**
     * 썸네일 조회용 Presigned URL 생성
     * fileKey가 null이면 null로 반환
     * fileKey 앞에 "thumbnail/" prefix를 추가하여 썸네일 이미지의 URL 생성
     * 썸네일이 존재하지 않으면 원본 이미지 URL 반환
     * @param fileKey 원본 S3 파일 키 (예: artwork/image123.png)
     * @return 썸네일 조회용 Presigned URL 문자열 (예: thumbnail/artwork/image123.png의 URL)
     */
    @Override
    public String generatePresignedUrlForThumbnail(String fileKey) {
        if (fileKey == null) {
            return null;
        }
        log.debug("썸네일용 Presigned URL 생성 시작 - 원본 키: {}", fileKey);

        String thumbnailKey = "thumbnail/" + fileKey;

        // 썸네일 파일 존재 여부 확인
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbnailKey)
                    .build();

            s3Client.headObject(headObjectRequest);

            // 썸네일이 존재하면 썸네일 URL 반환
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbnailKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES))
                    .getObjectRequest(getObjectRequest)
                    .build();

            log.debug("썸네일 발견, 썸네일 URL 반환 - 썸네일 키: {}", thumbnailKey);
            return s3Presigner.presignGetObject(presignRequest).url().toString();

        } catch (NoSuchKeyException e) {
            // 썸네일이 없으면 원본 이미지 URL 반환
            log.debug("썸네일을 찾을 수 없어 원본 이미지 URL을 반환합니다 - 원본 키: {}", fileKey);
            return generatePresignedUrlForDownload(fileKey);
        } catch (Exception e) {
            // 기타 에러 발생 시에도 원본 이미지 URL 반환
            log.warn("썸네일 확인 중 오류 발생, 원본 이미지 URL을 반환합니다 - 원본 키: {}, 오류: {}", fileKey, e.getMessage());
            return generatePresignedUrlForDownload(fileKey);
        }
    }

    /**
     * 여러 파일의 썸네일 조회용 Presigned URL 생성
     * fileKeys가 null이면 빈 리스트 반환, null 요소는 null로 변환
     * @param fileKeys 원본 S3 파일 키 리스트
     * @return 썸네일 조회용 Presigned URL 리스트
     */
    @Override
    public List<String> generatePresignedUrlsForThumbnail(List<String> fileKeys) {
        if (fileKeys == null) {
            return new ArrayList<>();
        }
        log.debug("썸네일용 Presigned URL 다중 생성 시작 - 파일 키 개수: {}", fileKeys.size());

        return fileKeys.stream()
                .map(this::generatePresignedUrlForThumbnail)
                .collect(Collectors.toList());
    }

    /**
     * 파일 삭제
     * @param fileKey 삭제할 S3 파일 키
     */
    @Override
    public void deleteFile(String fileKey) {
        log.info("S3 파일 삭제 시작 - 파일 키: {}", fileKey);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("S3 파일 삭제 완료 - 파일 키: {}", fileKey);
    }

    /**
     * 유니크한 파일 키 생성
     * @param folder 저장할 폴더
     * @return UUID 기반 유니크한 파일 키 (폴더/UUID)
     */
    private String generateFileKey(StorageFolder folder) {
        String uuid = UUID.randomUUID().toString();
        String fileKey = String.format("%s/%s", folder.getPath(), uuid);
        log.debug("새로운 파일 키 생성 - 폴더: {}, 키: {}", folder, fileKey);
        return fileKey;
    }
}
