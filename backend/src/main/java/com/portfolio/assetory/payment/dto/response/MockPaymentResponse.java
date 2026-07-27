package com.portfolio.assetory.payment.dto.response;
import java.math.BigDecimal;import java.time.LocalDateTime;import com.portfolio.assetory.payment.domain.Payment;
public record MockPaymentResponse(Long paymentId,Long orderId,BigDecimal amount,String paymentStatus,String orderStatus,LocalDateTime paidAt){public static MockPaymentResponse from(Payment p){return new MockPaymentResponse(p.getId(),p.getOrder().getId(),p.getAmount(),p.getStatus().name(),p.getOrder().getStatus().name(),p.getPaidAt());}}
