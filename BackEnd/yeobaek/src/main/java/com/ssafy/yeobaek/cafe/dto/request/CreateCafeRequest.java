package com.ssafy.yeobaek.cafe.dto.request;

import java.util.List;

import com.ssafy.yeobaek.common.validation.ValidCategoryIds;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카페 정보 등록 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCafeRequest {

	@Schema(description = "매장명", example = "카페 여백")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	@Size(min = 2, max = 15, message = "CAFE_INVALID_NAME_LENGTH")
	private String cafeName;

	@Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	private String address;

	@Schema(description = "카페 소개 (최대 500자)", example = "조용하고 아늑한 분위기의 카페입니다.")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	@Size(max = 500, message = "CAFE_INVALID_DESCRIPTION_LENGTH")
	private String description;

	@Schema(description = "연락처", example = "02-1234-5678")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	private String contact;

	@Schema(description = "영업 시작 시간 (HH:mm 형식)", example = "09:00")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	@Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "CAFE_INVALID_HOURS_FORMAT")
	private String businessHoursStart;

	@Schema(description = "영업 종료 시간 (HH:mm 형식)", example = "22:00")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	@Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "CAFE_INVALID_HOURS_FORMAT")
	private String businessHoursEnd;

	@Schema(description = "일일 평균 방문객 수", example = "100~200명")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	private String dailyVisitors;

	@Schema(description = "인테리어 스타일 (다중 선택)", example = "[1, 2]")
	@NotEmpty(message = "MISSING_REQUIRED_FIELD")
	@ValidCategoryIds
	private List<Long> categoryIds;

	@Schema(description = "카페 이미지 목록 (최소 3장, 최대 10장)")
	@Size(min = 3, max = 10, message = "CAFE_INVALID_IMAGES_LENGTH")
	private List<String> images;
}
