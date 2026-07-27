package com.portfolio.assetory.order.domain;

public enum OrderStatus {
	PENDING_PAYMENT,
	PAID,
	REFUND_REQUESTED,
	REFUND_APPROVED,
	REFUNDED,
	REFUND_REJECTED,
	PAYMENT_FAILED,
	CANCELED
}
