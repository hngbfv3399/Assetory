package com.portfolio.assetory.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.portfolio.assetory.product.domain.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false)
	private Order order;
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false)
	private Product product;
	@Column(nullable = false) private String productName;
	@Column(nullable = false, precision = 15, scale = 2) private BigDecimal unitPrice;
	@Column(nullable = false) private int quantity;
	@Column(nullable = false, precision = 15, scale = 2) private BigDecimal subtotalAmount;
	@Column(nullable = false, updatable = false) private LocalDateTime createdAt;
	protected OrderItem() {}
	public static OrderItem create(Order order, Product product) {
		OrderItem item = new OrderItem(); item.order = order; item.product = product; item.productName = product.getName();
		item.unitPrice = product.getPrice(); item.quantity = 1; item.subtotalAmount = product.getPrice(); return item;
	}
	@PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
	public Long getId() { return id; }
	public Order getOrder() { return order; }
	public Product getProduct() { return product; }
	public String getProductName() { return productName; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public int getQuantity() { return quantity; }
	public BigDecimal getSubtotalAmount() { return subtotalAmount; }
}
