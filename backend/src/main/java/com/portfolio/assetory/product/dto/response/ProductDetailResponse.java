package com.portfolio.assetory.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.domain.ProductSaleType;

public record ProductDetailResponse(
	Long id,
	Category category,
	Seller seller,
	String name,
	String summary,
	String description,
	BigDecimal price,
	ProductSaleType saleType,
	BigDecimal minimumPrice,
	LocalDateTime releaseAt,
	List<Image> images,
	BigDecimal averageRating,
	long reviewCount,
	LocalDateTime createdAt
) {
	public static ProductDetailResponse from(
		Product product,
		List<ProductImage> images,
		BigDecimal averageRating,
		long reviewCount
	) {
		return new ProductDetailResponse(
			product.getId(),
			new Category(product.getCategory().getId(), product.getCategory().getName()),
			new Seller(
				product.getSeller().getId(),
				product.getSeller().getNickname(),
				product.getSeller().getProfileImageUrl()
			),
			product.getName(),
			product.getSummary(),
			product.getDescription(),
			product.getPrice(),
			product.getSaleType(),
			product.getMinimumPrice(),
			product.getReleaseAt(),
			images.stream().map(Image::from).toList(),
			averageRating,
			reviewCount,
			product.getCreatedAt()
		);
	}

	public record Category(Long id, String name) {
	}

	public record Seller(Long id, String nickname, String profileImageUrl) {
	}

	public record Image(Long id, String imageUrl, boolean isThumbnail) {
		private static Image from(ProductImage image) {
			return new Image(
				image.getId(),
				image.getImageUrl(),
				image.getImageType() == ProductImageType.THUMBNAIL
			);
		}
	}
}
