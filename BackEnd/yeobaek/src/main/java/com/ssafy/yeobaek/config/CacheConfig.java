package com.ssafy.yeobaek.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 캐시 설정 클래스
 * Redis 캐시를 우선적으로 사용하고, Redis 장애 시 로컬 Caffeine 캐시로 폴백
 */
@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    // Presigned URL 캐시 만료 시간 (50분 - URL 유효기간 60분보다 짧게 설정하여 안전 마진 확보)
    private static final Long CACHE_TTL_MINUTES = 50L;

    // RefreshToken 만료 시간 (application.yaml에서 주입, 기본값 7일)
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * Redis 캐시 매니저 (Primary)
     * Redis 연결 실패 시에는 폴백 캐시 매니저가 사용됨
     */
    @Primary
    @Bean
    public CacheManager redisCacheManager() {
        log.info("Redis 캐시 매니저 생성 - TTL: {}분", CACHE_TTL_MINUTES);

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(CACHE_TTL_MINUTES))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    /**
     * Caffeine 로컬 메모리 캐시 매니저 (Fallback)
     * Redis 장애 시 자동으로 사용됨
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        log.info("Caffeine 로컬 캐시 매니저 생성 - TTL: {}분", CACHE_TTL_MINUTES);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(CACHE_TTL_MINUTES))
                .maximumSize(1000) // 최대 1000개 항목 저장
        );

        return cacheManager;
    }

    /**
     * RefreshToken용 Caffeine 로컬 캐시
     * Redis 장애 시 RefreshToken 저장을 위한 폴백 캐시
     */
    @Bean
    public Cache<String, String> refreshTokenCache() {
        long ttlMinutes = refreshTokenExpiration / 60000; // milliseconds to minutes
        log.info("RefreshToken Caffeine 캐시 생성 - TTL: {}분 ({}일)", ttlMinutes, ttlMinutes / 1440);

        return Caffeine.newBuilder()
                .expireAfterWrite(refreshTokenExpiration, TimeUnit.MILLISECONDS)
                .maximumSize(1000) // 최대 1000명의 RefreshToken 저장
                .build();
    }
}
