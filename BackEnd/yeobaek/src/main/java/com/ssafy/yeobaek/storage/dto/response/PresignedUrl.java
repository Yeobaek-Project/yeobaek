package com.ssafy.yeobaek.storage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Presigned URL 정보")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrl {

    @Schema(description = "파일 키", example = "artworks/550e8400-e29b-41d4-a716-446655440000")
    private String fileKey;

    @Schema(description = "업로드용 Presigned URL", example = "https://bucket.s3.amazonaws.com/...")
    private String uploadUrl;

    @Schema(description = "조회용 Presigned URL", example = "https://bucket.s3.amazonaws.com/...")
    private String downloadUrl;

    public static PresignedUrl of(String fileKey, String uploadUrl, String downloadUrl) {
        return PresignedUrl.builder()
                .fileKey(fileKey)
                .uploadUrl(uploadUrl)
                .downloadUrl(downloadUrl)
                .build();
    }
}
