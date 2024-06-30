package com.couponevent.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponListResponse {

    private Long couponId;
    private Long couponEventId;
    private String couponEventName;
    private LocalDateTime issuedAt;
    private LocalDateTime couponEventExpireAt;
}
