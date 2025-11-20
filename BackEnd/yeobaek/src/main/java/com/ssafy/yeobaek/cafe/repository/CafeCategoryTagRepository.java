package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeCategoryTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CafeCategoryTagRepository extends JpaRepository<CafeCategoryTag, Long> {

    List<CafeCategoryTag> findAllByCafe(Cafe cafe);
    
    List<CafeCategoryTag> findAllByCafe_IdIn(List<Long> cafeIds);

    void deleteAllByCafe(Cafe cafe);
}
