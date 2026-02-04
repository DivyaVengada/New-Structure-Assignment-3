package com.company.payment.payment_backend.repository;
import com.company.payment.payment_backend.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<PaymentOrder, Long> {
    PaymentOrder findByOrderId(String orderId);
}
