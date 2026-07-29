package com.portfolio.assetory.collaborator.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.collaborator.domain.ProductChangeRequest;

public interface ProductChangeRequestRepository extends JpaRepository<ProductChangeRequest, Long> {
	@Query("select request from ProductChangeRequest request join fetch request.requester where request.product.id = :productId order by request.requestedAt desc, request.id desc")
	List<ProductChangeRequest> findAllByProductIdWithRequester(@Param("productId") Long productId);

	@Query("select request from ProductChangeRequest request join fetch request.requester join fetch request.product where request.id = :id")
	Optional<ProductChangeRequest> findWithDetailsById(@Param("id") Long id);
}
