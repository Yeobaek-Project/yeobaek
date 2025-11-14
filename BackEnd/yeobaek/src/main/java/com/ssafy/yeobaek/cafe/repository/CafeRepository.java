package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CafeRepository extends JpaRepository<Cafe, Long>, JpaSpecificationExecutor<Cafe> {
    boolean existsByCafeOwner(User user);

    Optional<Cafe> findByCafeOwner(User user);

    Optional<Cafe> findByCafeOwnerId(Long ownerId);

    @Query("SELECT c FROM Cafe c ORDER BY (c.bookmarkCount * 9 + c.viewCount) DESC")
    List<Cafe> findPopularCafes(Pageable pageable);
}
