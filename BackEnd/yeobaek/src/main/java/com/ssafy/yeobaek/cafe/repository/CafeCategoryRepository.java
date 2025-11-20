package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.CafeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeCategoryRepository extends JpaRepository<CafeCategory, Long> {
}
