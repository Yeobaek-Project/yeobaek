package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.enums.ArtworkSortBy;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 작품 검색을 위한 JPA Specification
 */
public class ArtworkSpecification {

    /**
     * 검색 필터 조건 생성
     */
    public static Specification<Artwork> withFilters(
            String keyword,
            List<ArtCategory> categories,
            Integer minWidth,
            Integer maxWidth,
            Integer minHeight,
            Integer maxHeight
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Fetch Join으로 N+1 문제 방지
            if (query.getResultType().equals(Artwork.class)) {
                Fetch<Object, Object> artistFetch = root.fetch("artist", JoinType.LEFT);
                artistFetch.fetch("user", JoinType.LEFT);
            }

            // 키워드 검색 (작품명 OR 작가명)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                Join<Artwork, Artist> artistJoin = root.join("artist", JoinType.LEFT);
                Join<Artist, User> userJoin = artistJoin.join("user", JoinType.LEFT);

                Predicate titleLike = cb.like(cb.lower(root.get("title")), likePattern);
                Predicate nicknameLike = cb.like(cb.lower(userJoin.get("nickname")), likePattern);

                predicates.add(cb.or(titleLike, nicknameLike));
            }

            // 카테고리 필터
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("artCategory").in(categories));
            }

            // 규격 필터 - 가로
            if (minWidth != null) {
                predicates.add(cb.ge(root.get("widthCm"), minWidth.doubleValue()));
            }
            if (maxWidth != null) {
                predicates.add(cb.le(root.get("widthCm"), maxWidth.doubleValue()));
            }

            // 규격 필터 - 세로
            if (minHeight != null) {
                predicates.add(cb.ge(root.get("heightCm"), minHeight.doubleValue()));
            }
            if (maxHeight != null) {
                predicates.add(cb.le(root.get("heightCm"), maxHeight.doubleValue()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 정렬 조건 생성
     */
    public static Specification<Artwork> withSort(ArtworkSortBy sortBy) {
        return (root, query, cb) -> {
            if (sortBy == ArtworkSortBy.POPULARITY) {
                // 인기순: viewCount + bookmarkCount * 9 (내림차순)
                Expression<Integer> popularityScore = cb.sum(
                        root.get("viewCount"),
                        cb.prod(root.get("bookmarkCount"), 9)
                );
                query.orderBy(cb.desc(popularityScore));
            } else {
                // 최신순: createdAt (내림차순)
                query.orderBy(cb.desc(root.get("createdAt")));
            }
            return null;
        };
    }
}
