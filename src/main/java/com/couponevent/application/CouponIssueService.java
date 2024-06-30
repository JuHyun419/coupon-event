package com.couponevent.application;

import com.couponevent.domain.CouponEventEntity;
import com.couponevent.repository.CouponEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponEventJpaRepository couponEventJpaRepository;
    private final CouponRedisService couponRedisService;

    public boolean isFirstRequestFromUser(Long couponEventId, Long userId) {
        return couponRedisService.setHistoryIfNotExists(couponEventId, userId);
    }

    public boolean hasRemainingCoupon(Long couponEventId) {
        final CouponEventEntity couponEvent = this.getCouponEventById(couponEventId);

        return couponEvent.getIssueLimitCount() >= couponRedisService.getRequestSequentialNumber(couponEventId);
    }

    private CouponEventEntity getCouponEventById(Long couponEventId) {
        final CouponEventEntity cachedCouponEvent = couponRedisService.findCachedCouponEvent(couponEventId);

        if (Objects.isNull(cachedCouponEvent)) {
            log.debug("[{}]의 이벤트가 캐시에 존재하지 않습니다.", couponEventId);
            final CouponEventEntity couponEvent = couponEventJpaRepository.findById(couponEventId).orElseThrow();

            couponRedisService.saveCouponEventInCache(couponEvent);
            return couponEvent;
        }

        return cachedCouponEvent;
    }
}
