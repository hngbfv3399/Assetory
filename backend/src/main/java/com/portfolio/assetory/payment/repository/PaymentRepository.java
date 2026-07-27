package com.portfolio.assetory.payment.repository;
import org.springframework.data.jpa.repository.JpaRepository; import com.portfolio.assetory.payment.domain.Payment;
public interface PaymentRepository extends JpaRepository<Payment,Long>{boolean existsByOrderId(Long orderId);java.util.Optional<Payment> findByIdAndOrderBuyerId(Long paymentId,Long buyerId);java.util.Optional<Payment> findByOrderId(Long orderId);}
