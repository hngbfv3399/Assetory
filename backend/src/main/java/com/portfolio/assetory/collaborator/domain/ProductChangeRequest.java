package com.portfolio.assetory.collaborator.domain;

import java.time.LocalDateTime;

import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.product.domain.Product;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "product_change_requests")
public class ProductChangeRequest {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requester_id", nullable = false)
	private User requester;

	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reviewer_id")
	private User reviewer;

	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
	private ProductChangeType type;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
	private ProductChangeRequestStatus status;

	@Column(nullable = false, updatable = false)
	private LocalDateTime requestedAt;
	private LocalDateTime reviewedAt;
	@Column(columnDefinition = "TEXT")
	private String rejectionReason;

	protected ProductChangeRequest() {}

	public static ProductChangeRequest create(Product product, User requester, ProductChangeType type, String payload) {
		ProductChangeRequest request = new ProductChangeRequest();
		request.product = product;
		request.requester = requester;
		request.type = type;
		request.payload = payload;
		request.status = ProductChangeRequestStatus.PENDING;
		request.requestedAt = LocalDateTime.now();
		return request;
	}

	public void approve(User reviewer) {
		status = ProductChangeRequestStatus.APPROVED;
		this.reviewer = reviewer;
		reviewedAt = LocalDateTime.now();
	}

	public void reject(User reviewer, String reason) {
		status = ProductChangeRequestStatus.REJECTED;
		this.reviewer = reviewer;
		rejectionReason = reason;
		reviewedAt = LocalDateTime.now();
	}

	public Long getId() { return id; }
	public Product getProduct() { return product; }
	public User getRequester() { return requester; }
	public User getReviewer() { return reviewer; }
	public ProductChangeType getType() { return type; }
	public String getPayload() { return payload; }
	public ProductChangeRequestStatus getStatus() { return status; }
	public LocalDateTime getRequestedAt() { return requestedAt; }
	public LocalDateTime getReviewedAt() { return reviewedAt; }
	public String getRejectionReason() { return rejectionReason; }
}
