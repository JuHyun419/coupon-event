package com.couponevent.application;

import com.couponevent.application.dto.CouponListResponse;
import com.couponevent.domain.CouponEntity;
import com.couponevent.repository.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponGetService {

    private final CouponJpaRepository repository;

    public List<CouponListResponse> couponListByUserId(Long userId) {
        List<CouponEntity> coupons = repository.findAllByUserId(userId);

        return coupons.stream()
                .map(this::convertToCouponListResponse)
                .toList();
    }

    private CouponListResponse convertToCouponListResponse(CouponEntity couponEntity) {
        return new CouponListResponse(
                couponEntity.getId(),
                couponEntity.getCouponEventId(),
                couponEntity.getCouponEvent().getEventName(),
                couponEntity.getIssuedAt(),
                couponEntity.getCouponEvent().getExpireAt()
        );
    }
}
