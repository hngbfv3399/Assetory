package com.portfolio.assetory.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.domain.ProductResource;
import com.portfolio.assetory.product.domain.ProductResourceType;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.domain.ProductSaleType;

public record SellerProductDetailResponse(
	Long id,
	Long categoryId,
	String name,
	String summary,
	String description,
	BigDecimal price,
	ProductSaleType saleType,
	BigDecimal minimumPrice,
	LocalDateTime releaseAt,
	ProductStatus status,
	List<Image> images,
	List<Resource> resources
) {
	public static SellerProductDetailResponse from(
		Product product,
		List<ProductImage> images,
		List<ProductResource> resources
	) {
		return new SellerProductDetailResponse(
			product.getId(),
			product.getCategory().getId(),
			product.getName(),
			product.getSummary(),
			product.getDescription(),
			product.getPrice(),
			product.getSaleType(),
			product.getMinimumPrice(),
			product.getReleaseAt(),
			product.getStatus(),
			images.stream().map(Image::from).toList(),
			resources.stream().map(Resource::from).toList()
		);
	}

	public record Image(Long id, String imageUrl, boolean isThumbnail) {
		private static Image from(ProductImage image) {
			return new Image(image.getId(), image.getImageUrl(), image.getImageType() == ProductImageType.THUMBNAIL);
		}
	}

	public record Resource(Long id, String name, ProductResourceType type) {
		private static Resource from(ProductResource resource) {
			return new Resource(resource.getId(), resource.getDisplayName(), resource.getResourceType());
		}
	}
}
