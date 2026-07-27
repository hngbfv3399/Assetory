package com.portfolio.assetory.sellerorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.portfolio.assetory.order.domain.OrderItem;
import com.portfolio.assetory.payment.domain.Payment;
import com.portfolio.assetory.payment.domain.PaymentStatus;

public record SellerOrderDetailResponse(Long orderItemId, String orderNumber, String status, Product product,
	Buyer buyer, BigDecimal purchasedPrice, PaymentInfo payment, Refund refund, LocalDateTime purchasedAt) {

	public static SellerOrderDetailResponse from(OrderItem item, String thumbnailUrl, Payment payment) {
		return new SellerOrderDetailResponse(item.getId(), item.getOrder().getOrderNumber(), item.getOrder().getStatus().name(),
			new Product(item.getProduct().getId(), item.getProductName(), thumbnailUrl),
			new Buyer(item.getOrder().getBuyer().getId(), item.getOrder().getBuyer().getNickname()), item.getUnitPrice(),
			payment == null ? null : new PaymentInfo("MOCK", payment.getStatus() == PaymentStatus.SUCCESS ? "COMPLETED" : "FAILED", payment.getPaidAt()),
			null, item.getOrder().getCompletedAt() != null ? item.getOrder().getCompletedAt() : item.getOrder().getCreatedAt());
	}

	public record Product(Long id, String name, String thumbnailUrl) {}
	public record Buyer(Long id, String nickname) {}
	public record PaymentInfo(String method, String status, LocalDateTime paidAt) {}
	public record Refund(Long refundId, BigDecimal refundAmount, String reason, String status, LocalDateTime requestedAt) {}
}
