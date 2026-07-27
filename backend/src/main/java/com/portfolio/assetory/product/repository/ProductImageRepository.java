package com.portfolio.assetory.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	@Query("""
		select image
		from ProductImage image
		where image.product.id = :productId
		order by image.sortOrder asc, image.id asc
		""")
	List<ProductImage> findByProductIdOrderBySortOrderAscIdAsc(@Param("productId") Long productId);

	@Query("""
		select image
		from ProductImage image
		where image.product.id in :productIds
		  and image.imageType = :imageType
		order by image.product.id asc, image.sortOrder asc, image.id asc
		""")
	List<ProductImage> findThumbnailsByProductIds(
		@Param("productIds") List<Long> productIds,
		@Param("imageType") ProductImageType imageType
	);

	@Query("select case when count(image) > 0 then true else false end from ProductImage image where image.product.id = :productId and image.imageType = :imageType")
	boolean existsByProductIdAndImageType(@Param("productId") Long productId, @Param("imageType") ProductImageType imageType);

	@Query("select image from ProductImage image where image.id = :imageId and image.product.id = :productId")
	Optional<ProductImage> findByIdAndProductId(@Param("imageId") Long imageId, @Param("productId") Long productId);
}
