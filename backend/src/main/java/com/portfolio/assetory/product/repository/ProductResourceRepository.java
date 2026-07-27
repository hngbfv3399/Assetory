package com.portfolio.assetory.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.product.domain.ProductResource;

public interface ProductResourceRepository extends JpaRepository<ProductResource, Long> {

	List<ProductResource> findByProductIdAndActiveTrueOrderBySortOrderAscIdAsc(Long productId);

	@Query("select case when count(resource) > 0 then true else false end from ProductResource resource where resource.product.id = :productId and resource.active = true")
	boolean existsByProductIdAndActiveTrue(@Param("productId") Long productId);

	long countByProductId(Long productId);

	@Query("select resource from ProductResource resource where resource.id = :resourceId and resource.product.id = :productId")
	Optional<ProductResource> findByIdAndProductId(@Param("resourceId") Long resourceId, @Param("productId") Long productId);
}
