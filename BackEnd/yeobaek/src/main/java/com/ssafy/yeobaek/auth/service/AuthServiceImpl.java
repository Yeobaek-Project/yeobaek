package com.ssafy.yeobaek.auth.service;

import com.ssafy.yeobaek.auth.dto.request.LoginRequest;
import com.ssafy.yeobaek.auth.dto.request.SignupRequest;
import com.ssafy.yeobaek.auth.dto.response.EmailCheckData;
import com.ssafy.yeobaek.auth.dto.response.LoginData;
import com.ssafy.yeobaek.auth.dto.response.TokenData;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.common.util.JwtUtil;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService{

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return EmailCheckData (available: true면 사용 가능, false면 이미 사용 중)
     */
    @Override
    public EmailCheckData checkEmailDuplicate(String email) {
        log.info("이메일 중복 체크 시작 - 이메일: {}", email);
        boolean exists = userService.isEmailExists(email);
        boolean available = !exists; // exists의 반대 (존재하면 사용 불가)
        log.info("이메일 중복 체크 완료 - 사용 가능 여부: {}", available);
        return new EmailCheckData(email, available);
    }

    /**
     * 회원가입
     * @param request 회원가입 요청 정보
     */
    @Override
    @Transactional
    public void signup(SignupRequest request) {
        log.info("회원가입 시작 - 이메일: {}, 사용자 유형: {}", request.getEmail(), request.getUserType());
        // 이메일 중복 검증
        if (userService.isEmailExists(request.getEmail())) {
            log.warn("회원가입 실패 - 이미 존재하는 이메일: {}", request.getEmail());
            throw new CustomException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 일치 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            log.warn("회원가입 실패 - 비밀번호 불일치 - 이메일: {}", request.getEmail());
            throw new CustomException(ResponseCode.PASSWORD_MISMATCH);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 생성 및 저장
        userService.createUser(request, encodedPassword);
        log.info("회원가입 완료 - 이메일: {}", request.getEmail());
    }

    /**
     * 로그인
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return LoginData (accessToken, refreshToken, userType)
     */
    @Override
    public LoginData login(LoginRequest request) {
        log.info("로그인 시작 - 이메일: {}", request.getEmail());
        // 이메일로 사용자 조회
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 사용자: {}", request.getEmail());
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: {}", request.getEmail());
            throw new CustomException(ResponseCode.INVALID_CREDENTIALS);
        }

        // JWT Access Token 생성 (userId, userType 포함)
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserType());

        // JWT Refresh Token 생성 (userId만 포함)
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Refresh Token을 Redis에 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        log.info("로그인 성공 - 사용자 ID: {}, 사용자 유형: {}", user.getId(), user.getUserType());
        // LoginData 반환
        return new LoginData(accessToken, refreshToken, user.getId(), user.getUserType());
    }

    /**
     * 로그아웃
     * - Redis에서 Refresh Token 삭제
     */
    @Override
    public void logout() {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("로그아웃 시작 - 사용자 ID: {}", userId);

        // Redis에서 Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId);
        log.info("로그아웃 완료 - 사용자 ID: {}", userId);
    }

    /**
     * Access Token 재발급
     * @param refreshToken Refresh Token
     * @return TokenData (새로운 accessToken, refreshToken)
     */
    @Override
    public TokenData reissue(String refreshToken) {
        log.info("Access Token 재발급 시작");
        // Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("토큰 재발급 실패 - 유효하지 않은 Refresh Token");
            throw new CustomException(ResponseCode.INVALID_TOKEN);
        }

        // Refresh Token에서 userId 추출
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        log.info("토큰 재발급 요청 - 사용자 ID: {}", userId);

        // Redis에 저장된 Refresh Token과 비교
        if (!refreshTokenService.validateRefreshToken(userId, refreshToken)) {
            log.warn("토큰 재발급 실패 - Redis의 토큰과 불일치 - 사용자 ID: {}", userId);
            throw new CustomException(ResponseCode.INVALID_TOKEN);
        }

        // 사용자 정보 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("토큰 재발급 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserType());

        // 새로운 Refresh Token 생성
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Redis에 새로운 Refresh Token 저장
        refreshTokenService.saveRefreshToken(user.getId(), newRefreshToken);

        log.info("Access Token 재발급 완료 - 사용자 ID: {}", userId);
        // 새 토큰들 반환
        return new TokenData(newAccessToken, newRefreshToken);
    }
}
