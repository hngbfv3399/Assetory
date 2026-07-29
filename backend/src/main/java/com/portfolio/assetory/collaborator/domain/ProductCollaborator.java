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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "product_collaborators",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_product_collaborators_product_user",
		columnNames = {"product_id", "user_id"}
	)
)
public class ProductCollaborator {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductCollaboratorRole role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductCollaboratorStatus status;

	@Column(nullable = false, updatable = false)
	private LocalDateTime invitedAt;

	private LocalDateTime respondedAt;

	private LocalDateTime removedAt;

	protected ProductCollaborator() {
	}

	public static ProductCollaborator invite(Product product, User user, ProductCollaboratorRole role) {
		ProductCollaborator collaborator = new ProductCollaborator();
		collaborator.product = product;
		collaborator.user = user;
		collaborator.role = role;
		collaborator.status = ProductCollaboratorStatus.PENDING;
		collaborator.invitedAt = LocalDateTime.now();
		return collaborator;
	}

	public void reinvite(ProductCollaboratorRole role) {
		this.role = role;
		status = ProductCollaboratorStatus.PENDING;
		respondedAt = null;
		removedAt = null;
	}

	public void accept() {
		status = ProductCollaboratorStatus.ACCEPTED;
		respondedAt = LocalDateTime.now();
	}

	public void reject() {
		status = ProductCollaboratorStatus.REJECTED;
		respondedAt = LocalDateTime.now();
	}

	public void remove() {
		status = ProductCollaboratorStatus.REMOVED;
		removedAt = LocalDateTime.now();
	}

	public void changeRole(ProductCollaboratorRole role) {
		this.role = role;
	}

	public Long getId() { return id; }
	public Product getProduct() { return product; }
	public User getUser() { return user; }
	public ProductCollaboratorRole getRole() { return role; }
	public ProductCollaboratorStatus getStatus() { return status; }
	public LocalDateTime getInvitedAt() { return invitedAt; }
	public LocalDateTime getRespondedAt() { return respondedAt; }
	public LocalDateTime getRemovedAt() { return removedAt; }
}
