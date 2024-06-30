package com.couponevent.api;

import com.couponevent.application.CouponGetService;
import com.couponevent.application.dto.CouponListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponGetController {

    private final CouponGetService service;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CouponListResponse>> couponListByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(
                service.couponListByUserId(userId)
        );
    }
}
