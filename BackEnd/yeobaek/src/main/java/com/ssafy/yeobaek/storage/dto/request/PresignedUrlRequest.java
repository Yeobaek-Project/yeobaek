package com.ssafy.yeobaek.storage.dto.request;

import com.ssafy.yeobaek.common.enums.StorageFolder;
import com.ssafy.yeobaek.common.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Presigned URL 생성 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {

    @Schema(description = "저장 폴더", example = "ARTWORK")
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @ValidEnum(enumClass = StorageFolder.class, message = "INVALID_STORAGE_FOLDER")
    private String folder;

    @Schema(description = "생성할 URL 개수 (1~10)", example = "3")
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Min(value = 1, message = "INVALID_FILE_COUNT")
    @Max(value = 10, message = "INVALID_FILE_COUNT")
    private Integer count;
}
