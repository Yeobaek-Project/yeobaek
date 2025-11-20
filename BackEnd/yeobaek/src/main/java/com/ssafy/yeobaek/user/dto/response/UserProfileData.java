package com.ssafy.yeobaek.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "사용자 프로필 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileData {
    @Schema(description = "사용자 기본 정보")
    private UserData user;

    @Schema(description = "작가 정보 (ARTIST인 경우만)")
    private ArtistData artist;

    @Schema(description = "카페 정보 (CAFE_OWNER인 경우만)")
    private CafeData cafe;
}
