package com.ssafy.yeobaek.storage.service;

import com.ssafy.yeobaek.storage.dto.request.PresignedUrlRequest;
import com.ssafy.yeobaek.storage.dto.response.PresignedUrl;
import com.ssafy.yeobaek.storage.dto.response.PresignedUrlListData;

import java.util.List;

public interface S3Service {

    /**
     * 다중 Presigned URL 쌍 생성 (업로드용 + 조회용)
     * @param request Presigned URL 생성 요청 정보 (저장 폴더, 개수)
     * @return Presigned URL 리스트 응답 (fileKey, uploadUrl, downloadUrl)
     */
    PresignedUrlListData generatePresignedUrlsForUpload(PresignedUrlRequest request);

    /**
     * 단일 Presigned URL 쌍 생성 (업로드용 + 조회용)
     * @param fileKey S3 파일 키
     * @return Presigned URL 쌍 (fileKey, uploadUrl, downloadUrl)
     */
    PresignedUrl generatePresignedUrlPair(String fileKey);

    /**
     * 업로드용 Presigned URL 생성
     * @param fileKey S3 파일 키
     * @return 업로드용 Presigned URL 문자열
     */
    String generatePresignedUrlForUpload(String fileKey);

    /**
     * 조회용 Presigned URL 생성
     * fileKey가 null이면 null로 반환
     * @param fileKey S3 파일 키
     * @return 조회용 Presigned URL 문자열
     */
    String generatePresignedUrlForDownload(String fileKey);

    /**
     * 여러 파일의 조회용 Presigned URL 생성
     * fileKeys가 null이면 빈 리스트 반환, null 요소는 null로 변환
     * @param fileKeys S3 파일 키 리스트
     * @return 조회용 Presigned URL 리스트
     */
    List<String> generatePresignedUrlsForDownload(List<String> fileKeys);

    /**
     * 썸네일 조회용 Presigned URL 생성
     * fileKey가 null이면 null로 반환
     * fileKey 앞에 "thumbnail/" prefix를 추가하여 썸네일 이미지의 URL 생성
     * @param fileKey 원본 S3 파일 키 (예: artwork/image123.png)
     * @return 썸네일 조회용 Presigned URL 문자열 (예: thumbnail/artwork/image123.png의 URL)
     */
    String generatePresignedUrlForThumbnail(String fileKey);

    /**
     * 여러 파일의 썸네일 조회용 Presigned URL 생성
     * fileKeys가 null이면 빈 리스트 반환, null 요소는 null로 변환
     * @param fileKeys 원본 S3 파일 키 리스트
     * @return 썸네일 조회용 Presigned URL 리스트
     */
    List<String> generatePresignedUrlsForThumbnail(List<String> fileKeys);

    /**
     * 파일 삭제
     * @param fileKey 삭제할 S3 파일 키
     */
    void deleteFile(String fileKey);
}
