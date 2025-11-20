package com.ssafy.yeobaek.storage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Presigned URL 리스트 응답 데이터")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlListData {

    @Schema(description = "Presigned URL 목록")
    private List<PresignedUrl> presignedUrls;
}
