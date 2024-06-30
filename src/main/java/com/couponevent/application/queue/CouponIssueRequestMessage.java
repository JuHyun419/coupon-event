package com.couponevent.application.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssueRequestMessage {
    private Long couponEventId;
    private Long userId;

    public static CouponIssueRequestMessage from(Long couponEventId, Long userId) {
        return new CouponIssueRequestMessage(couponEventId, userId);
    }
}
