package com.portfolio.assetory.wishlist.domain;

import java.time.LocalDateTime;

import com.portfolio.assetory.member.domain.User;
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "wishlists",
	uniqueConstraints = @UniqueConstraint(name = "uk_wishlists_user_product", columnNames = {"user_id", "product_id"})
)
public class Wishlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected Wishlist() {
	}

	public static Wishlist create(User user, Product product) {
		Wishlist wishlist = new Wishlist();
		wishlist.user = user;
		wishlist.product = product;
		return wishlist;
	}

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Product getProduct() {
		return product;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
