package com.couponevent.application;

import com.couponevent.domain.CouponEventEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRedisService {

    private static final String COUPON_EVENT_KEY_PREFIX = "coupon_event.v1:";
    private static final Long COUPON_EVENT_EXPIRE_SECONDS = 60 * 3L; // 3분

    private static final String USER_REQUEST_HISTORY_KEY_PREFIX = "coupon-history.user-request.v1:";
    private static final String REQUEST_COUNT_HISTORY_KEY_PREFIX = "coupon-history.request-count.v1:";
    private static final Long COUPON_ISSUE_HISTORY_EXPIRE_SECONDS = 60 * 60 * 24 * 3L; // 3일

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CouponEventEntity findCachedCouponEvent(Long couponEventId) {
        final String couponEventJsonString = redisTemplate.opsForValue().get(this.generateCouponEventCacheKey(couponEventId));
        if (Objects.isNull(couponEventJsonString)) return null;

        try {
            return objectMapper.readValue(couponEventJsonString, CouponEventEntity.class);
        } catch (JsonProcessingException e) {
            // TODO: logging
            throw new RuntimeException(e);
        }
    }

    public void saveCouponEventInCache(CouponEventEntity couponEvent) {
        String couponEventJsonString;
        try {
            couponEventJsonString = objectMapper.writeValueAsString(couponEvent);
        } catch (JsonProcessingException e) {
            // TODO: logging
            throw new RuntimeException(e);
        }

        // coupon_event.v1:couponEventId
        redisTemplate.opsForValue().set(
                this.generateCouponEventCacheKey(couponEvent.getId()),
                couponEventJsonString,
                Duration.ofSeconds(COUPON_EVENT_EXPIRE_SECONDS)
        );
    }

    private String generateCouponEventCacheKey(Long couponEventId) {
        return COUPON_EVENT_KEY_PREFIX + couponEventId;
    }

    /**
     * 쿠폰 이벤트 내에서, 유저의 발급 요청 이력이 없다면 저장
     * @param couponEventId 쿠폰 이벤트 ID
     * @param userId 유저 ID
     * @return 최초 요청: true, 그 외 요청: false
     */
    public boolean setHistoryIfNotExists(Long couponEventId, Long userId) {
        // coupon-history.user-request.v1:couponEventId:userId
        return redisTemplate.opsForValue().setIfAbsent(
                this.generateUserRequestHistoryCacheKey(couponEventId, userId),
                "1",
                Duration.ofSeconds(COUPON_ISSUE_HISTORY_EXPIRE_SECONDS)
        );
    }

    /**
     * 쿠폰 이벤트 내에서, 발급 요청을 몇번째로 했는지 확인
     * @param couponEventId 쿠폰 이벤트 ID
     * @return
     */
    public Long getRequestSequentialNumber(Long couponEventId) {
        // "coupon-history.request-count.v1:couponEventId
        final String key = this.generateRequestCountHistoryCacheKey(couponEventId);
        final Long requestSequentialNumber = redisTemplate.opsForValue().increment(key); // 0, 1, 2, ...

        // 최초 쿠폰 이벤트의 발급 요청에 대해 TTL 설정
        if (isFirstRequest(requestSequentialNumber)) {
            setEventSequentialNumberTtl(key);
        }

        return requestSequentialNumber;
    }

    private static boolean isFirstRequest(Long requestSequentialNumber) {
        return requestSequentialNumber != null && requestSequentialNumber == 1L;
    }

    private void setEventSequentialNumberTtl(String key) {
        redisTemplate.expire(key, Duration.ofSeconds(COUPON_ISSUE_HISTORY_EXPIRE_SECONDS));
    }

    private String generateUserRequestHistoryCacheKey(Long couponEventId, Long userId) {
        return USER_REQUEST_HISTORY_KEY_PREFIX + couponEventId + ":" + userId;
    }

    private String generateRequestCountHistoryCacheKey(Long couponEventId) {
        return REQUEST_COUNT_HISTORY_KEY_PREFIX + couponEventId;
    }

}
