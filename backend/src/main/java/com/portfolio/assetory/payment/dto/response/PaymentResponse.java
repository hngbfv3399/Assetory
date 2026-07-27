package com.portfolio.assetory.payment.dto.response;
import java.math.BigDecimal;import java.time.LocalDateTime;import com.portfolio.assetory.payment.domain.Payment;
public record PaymentResponse(Long paymentId,Long orderId,BigDecimal amount,String status,LocalDateTime paidAt){public static PaymentResponse from(Payment p){return new PaymentResponse(p.getId(),p.getOrder().getId(),p.getAmount(),p.getStatus().name(),p.getPaidAt());}}
