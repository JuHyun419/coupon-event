package com.couponevent.application.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.couponevent.common.Topic.COUPON_ISSUE_REQUEST_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(Long couponEventId, Long userId) {
        final CouponIssueRequestMessage message = CouponIssueRequestMessage.from(couponEventId, userId);

        try {
            kafkaTemplate.send(COUPON_ISSUE_REQUEST_TOPIC, message.getUserId().toString(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Send to Kafka failed, coupon_event_id: {}, user_id: {}, message: {}", couponEventId, userId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
