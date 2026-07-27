package com.portfolio.assetory.product.domain;

import java.time.LocalDateTime;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_resources")
public class ProductResource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductResourceType resourceType;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String resourceUrl;

	private String originalName;

	@Column(nullable = false)
	private String displayName;

	private Long fileSize;

	@Column(nullable = false)
	private int sortOrder;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	protected ProductResource() {
	}

	public static ProductResource attach(Product product, ProductResourceType type, String url, String originalName, String displayName, Long fileSize, int sortOrder) {
		ProductResource resource = new ProductResource();
		resource.product = product; resource.resourceType = type; resource.resourceUrl = url; resource.originalName = originalName;
		resource.displayName = displayName; resource.fileSize = fileSize; resource.sortOrder = sortOrder;
		return resource;
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		active = true;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public ProductResourceType getResourceType() {
		return resourceType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void update(String displayName, String resourceUrl) {
		if (displayName != null) this.displayName = displayName;
		if (resourceUrl != null) this.resourceUrl = resourceUrl;
	}
}
