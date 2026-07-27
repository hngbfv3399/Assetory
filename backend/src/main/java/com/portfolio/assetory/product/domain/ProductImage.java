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
import jakarta.persistence.Table;

@Entity
@Table(name = "product_images")
public class ProductImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private String imageUrl;

	private String originalName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductImageType imageType;

	@Column(nullable = false)
	private int sortOrder;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected ProductImage() {
	}

	public static ProductImage attach(
		Product product,
		String imageUrl,
		String originalName,
		ProductImageType imageType,
		int sortOrder
	) {
		ProductImage image = new ProductImage();
		image.product = product;
		image.imageUrl = imageUrl;
		image.originalName = originalName;
		image.imageType = imageType;
		image.sortOrder = sortOrder;
		return image;
	}

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Long getProductId() {
		return product.getId();
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public ProductImageType getImageType() {
		return imageType;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void makeThumbnail() { imageType = ProductImageType.THUMBNAIL; }
	public void makeDetail() { imageType = ProductImageType.DETAIL; }
}
