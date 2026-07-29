package com.portfolio.assetory.collaborator.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.collaborator.domain.ProductCollaborator;
import com.portfolio.assetory.collaborator.domain.ProductCollaboratorStatus;
import com.portfolio.assetory.collaborator.domain.ProductCollaboratorRole;

public interface ProductCollaboratorRepository extends JpaRepository<ProductCollaborator, Long> {
	Optional<ProductCollaborator> findByProductIdAndUserId(Long productId, Long userId);

	@Query("select collaborator from ProductCollaborator collaborator join fetch collaborator.user where collaborator.product.id = :productId order by collaborator.invitedAt desc, collaborator.id desc")
	List<ProductCollaborator> findAllByProductIdWithUser(@Param("productId") Long productId);

	boolean existsByProductIdAndUserIdAndStatus(Long productId, Long userId, ProductCollaboratorStatus status);

	boolean existsByProductIdAndUserIdAndStatusAndRoleIn(Long productId, Long userId, ProductCollaboratorStatus status, java.util.Collection<ProductCollaboratorRole> roles);

	Optional<ProductCollaborator> findByProductIdAndUserIdAndStatus(Long productId, Long userId, ProductCollaboratorStatus status);
}
