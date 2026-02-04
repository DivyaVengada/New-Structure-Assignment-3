package com.company.payment.payment_backend.repository;

import com.company.payment.payment_backend.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    WebhookEvent findByEventId(String eventId);
}
