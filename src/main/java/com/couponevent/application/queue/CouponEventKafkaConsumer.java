package com.couponevent.application.queue;

import com.couponevent.common.Topic;
import com.couponevent.domain.CouponEntity;
import com.couponevent.repository.CouponJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventKafkaConsumer {

    private final CouponJpaRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = {Topic.COUPON_ISSUE_REQUEST_TOPIC},
            groupId = "coupon-issue-request",
            concurrency = "3"
    )
    public void listen(ConsumerRecord<String, String> record) throws JsonProcessingException {
        final CouponIssueRequestMessage message = objectMapper.readValue(record.value(), CouponIssueRequestMessage.class);
        final CouponEntity coupon = new CouponEntity(message.getCouponEventId(), message.getUserId());

        repository.save(coupon);
    }
}
