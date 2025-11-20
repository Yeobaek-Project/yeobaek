package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 카페 검색을 위한 JPA Specification
 */
public class CafeSpecification {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * 검색 필터 조건 생성
     *
     * @param keyword       검색 키워드 (카페명)
     * @param centerLatitude 지도 중심 위도
     * @param centerLongitude 지도 중심 경도
     * @param radiusKm      조회 반경 (km)
     * @param minWidth      최소 너비
     * @param maxWidth      최대 너비
     * @param minHeight     최소 높이
     * @param maxHeight     최대 높이
     * @param minCommission 최소 수수료
     * @param maxCommission 최대 수수료
     * @return Specification<Cafe> 검색 조건
     */
    public static Specification<Cafe> withFilters(
            String keyword,
            Double centerLatitude,
            Double centerLongitude,
            Double radiusKm,
            Integer minWidth,
            Integer maxWidth,
            Integer minHeight,
            Integer maxHeight,
            Double minCommission,
            Double maxCommission
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // DisplayWall, DisplaySlot을 조인하여 필터링에 사용
            Join<Cafe, DisplayWall> wallJoin = root.join("displayWalls", JoinType.LEFT);
            Join<DisplayWall, DisplaySlot> slotJoin = wallJoin.join("slot", JoinType.LEFT);

            // 키워드 검색 (카페명)
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("cafeName")), "%" + keyword.toLowerCase() + "%"));
            }

            // 위치 기반 필터링 (지도 중심 좌표 및 반경)
            if (centerLatitude != null && centerLongitude != null && radiusKm != null) {
                Expression<Double> latRad = cb.function("RADIANS", Double.class, root.get("latitude"));
                Expression<Double> lonRad = cb.function("RADIANS", Double.class, root.get("longitude"));
                Expression<Double> centerLatRad = cb.literal(Math.toRadians(centerLatitude));
                Expression<Double> centerLonRad = cb.literal(Math.toRadians(centerLongitude));

                Expression<Double> deltaLatDiff = cb.function("DIFF", Double.class, latRad, centerLatRad);
                Expression<Double> deltaLat = cb.function("DIV", Double.class, deltaLatDiff, cb.literal(2.0));

                Expression<Double> deltaLonDiff = cb.function("DIFF", Double.class, lonRad, centerLonRad);
                Expression<Double> deltaLon = cb.function("DIV", Double.class, deltaLonDiff, cb.literal(2.0));

                Expression<Double> sinDeltaLat = cb.function("SIN", Double.class, deltaLat);
                Expression<Double> sinDeltaLon = cb.function("SIN", Double.class, deltaLon);

                Expression<Double> a = cb.function("SUM_FUNC", Double.class,
                        cb.function("PROD", Double.class, sinDeltaLat, sinDeltaLat),
                        cb.function("PROD", Double.class,
                                cb.function("PROD", Double.class, cb.function("COS", Double.class, centerLatRad), cb.function("COS", Double.class, latRad)),
                                cb.function("PROD", Double.class, sinDeltaLon, sinDeltaLon)
                        )
                );

                Expression<Double> c = cb.function("PROD", Double.class, cb.literal(2.0),
                        cb.function("ATAN2", Double.class, cb.function("SQRT", Double.class, a), cb.function("SQRT", Double.class, cb.function("DIFF", Double.class, cb.literal(1.0), a))));
                Expression<Double> distance = cb.function("PROD", Double.class, cb.literal(EARTH_RADIUS_KM), c);

                predicates.add(cb.le(distance, radiusKm));
            }

            // 규격 필터 - 가로
            if (minWidth != null) {
                predicates.add(cb.ge(wallJoin.get("widthCm"), minWidth));
            }
            if (maxWidth != null) {
                predicates.add(cb.le(wallJoin.get("widthCm"), maxWidth));
            }

            // 규격 필터 - 세로
            if (minHeight != null) {
                predicates.add(cb.ge(wallJoin.get("heightCm"), minHeight));
            }
            if (maxHeight != null) {
                predicates.add(cb.le(wallJoin.get("heightCm"), maxHeight));
            }

            // 수수료 필터
            if (minCommission != null) {
                predicates.add(cb.ge(slotJoin.get("commissionRate"), minCommission));
            }
            if (maxCommission != null) {
                predicates.add(cb.le(slotJoin.get("commissionRate"), maxCommission));
            }

            // 중복 결과 방지를 위해 DISTINCT 적용
            query.distinct(true);

            // 인기 점수 계산 및 정렬 (조회수 1 : 북마크수 9 비율)
            Expression<Double> popularityScore = cb.function("SUM_FUNC", Double.class,
                    cb.function("PROD", Double.class, root.get("viewCount"), cb.literal(0.1)),
                    cb.function("PROD", Double.class, root.get("bookmarkCount"), cb.literal(0.9))
            );
            query.orderBy(cb.desc(popularityScore));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}