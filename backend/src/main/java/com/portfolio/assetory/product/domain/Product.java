package com.portfolio.assetory.product.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.assetory.category.domain.Category;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String summary;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "TEXT")
	private String usageGuide;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductStatus status;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ProductImage> images = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime publishedAt;

	private LocalDateTime deletedAt;

	protected Product() {
	}

	public static Product create(
		User seller,
		Category category,
		String name,
		String summary,
		String description,
		String usageGuide,
		BigDecimal price
	) {
		Product product = new Product();
		product.seller = seller;
		product.category = category;
		product.name = name;
		product.summary = summary;
		product.description = description;
		product.usageGuide = usageGuide;
		product.price = price;
		product.status = ProductStatus.DRAFT;
		return product;
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public boolean isOnSale() {
		return status == ProductStatus.ON_SALE && deletedAt == null;
	}

	public void startSale() {
		status = ProductStatus.ON_SALE;
		publishedAt = LocalDateTime.now();
	}

	public void stopSale() { status = ProductStatus.STOPPED; }

	public void update(Category category, String name, String summary, String description, BigDecimal price) {
		if (category != null) this.category = category;
		if (name != null) this.name = name;
		if (summary != null) this.summary = summary;
		if (description != null) this.description = description;
		if (price != null) this.price = price;
	}

	public void delete() {
		deletedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public User getSeller() {
		return seller;
	}

	public Category getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public List<ProductImage> getImages() {
		return images;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
