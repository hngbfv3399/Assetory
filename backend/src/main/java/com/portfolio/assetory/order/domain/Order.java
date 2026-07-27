package com.portfolio.assetory.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.portfolio.assetory.member.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity(name = "PurchaseOrder")
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 40)
	private String orderNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_id", nullable = false)
	private User buyer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OrderStatus status;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime completedAt;
	private LocalDateTime cancelledAt;

	protected Order() {}

	public static Order create(String orderNumber, User buyer, User seller, BigDecimal totalAmount) {
		Order order = new Order();
		order.orderNumber = orderNumber;
		order.buyer = buyer;
		order.seller = seller;
		order.totalAmount = totalAmount;
		order.status = OrderStatus.PENDING_PAYMENT;
		return order;
	}

	@PrePersist
	void onCreate() { createdAt = LocalDateTime.now(); }

	public void markPaid() { status = OrderStatus.PAID; completedAt = LocalDateTime.now(); }
	public void markPaymentFailed() { status = OrderStatus.PAYMENT_FAILED; }
	public void markRefundRequested() { status = OrderStatus.REFUND_REQUESTED; }
	public void restorePaid() { status = OrderStatus.PAID; }
	public void markRefundApproved() { status = OrderStatus.REFUND_APPROVED; }
	public void markRefundRejected() { status = OrderStatus.REFUND_REJECTED; }
	public void markRefunded() { status = OrderStatus.REFUNDED; }
	public Long getId() { return id; }
	public String getOrderNumber() { return orderNumber; }
	public User getBuyer() { return buyer; }
	public User getSeller() { return seller; }
	public BigDecimal getTotalAmount() { return totalAmount; }
	public OrderStatus getStatus() { return status; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getCompletedAt() { return completedAt; }
}
