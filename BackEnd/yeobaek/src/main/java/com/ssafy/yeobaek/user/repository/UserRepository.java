package com.ssafy.yeobaek.user.repository;

import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 아니면 false
     */
    boolean existsByEmail(String email);

    /**
     * 이메일로 사용자 조회
     * @param email 조회할 이메일
     * @return 사용자 엔티티 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임으로 검색
     * @param keyword 검색 키워드
     * @param userType 사용자 유형
     * @return 닉네임에 키워드가 포함된 작가 목록
     */
    List<User> findByNicknameContainingAndUserType(String keyword, UserRole userType);
}
