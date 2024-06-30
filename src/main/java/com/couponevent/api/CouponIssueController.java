package com.couponevent.api;

import com.couponevent.application.CouponIssueService;
import com.couponevent.application.dto.CouponIssueRequest;
import com.couponevent.application.queue.CouponEventKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponIssueController {

    private final CouponIssueService service;
    private final CouponEventKafkaProducer kafkaProducer;

    @PostMapping
    public ResponseEntity<String> issue(@RequestBody CouponIssueRequest request) {
        if (!service.isFirstRequestFromUser(request.getCouponEventId(), request.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 발급받은 쿠폰입니다.");
        }

        if (!service.hasRemainingCoupon(request.getCouponEventId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("쿠폰 잔여량이 존재하지 않습니다.");
        }

        kafkaProducer.sendMessage(request.getCouponEventId(), request.getUserId());

        return ResponseEntity.ok("쿠폰 발급에 성공하였습니다.");
    }
}
