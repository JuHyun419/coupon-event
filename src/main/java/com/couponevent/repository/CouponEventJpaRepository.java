package com.couponevent.repository;

import com.couponevent.domain.CouponEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponEventJpaRepository extends JpaRepository<CouponEventEntity, Long> {
}
