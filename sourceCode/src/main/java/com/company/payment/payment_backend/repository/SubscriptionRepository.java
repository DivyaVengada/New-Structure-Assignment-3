package com.company.payment.payment_backend.repository;
import com.company.payment.payment_backend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findBySubscriptionId(String subscriptionId);
    Subscription findByGatewaySubscriptionId(String gatewaySubscriptionId);
}
