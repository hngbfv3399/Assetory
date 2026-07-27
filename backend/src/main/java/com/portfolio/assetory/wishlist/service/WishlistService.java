package com.portfolio.assetory.wishlist.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.repository.UserRepository;
import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.product.repository.ProductRepository;
import com.portfolio.assetory.wishlist.domain.Wishlist;
import com.portfolio.assetory.wishlist.dto.response.WishlistListResponse;
import com.portfolio.assetory.wishlist.dto.response.WishlistProductResponse;
import com.portfolio.assetory.wishlist.dto.response.WishlistStatusResponse;
import com.portfolio.assetory.wishlist.repository.WishlistRepository;

@Service
@Transactional(readOnly = true)
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;

	public WishlistService(
		WishlistRepository wishlistRepository,
		UserRepository userRepository,
		ProductRepository productRepository,
		ProductImageRepository productImageRepository
	) {
		this.wishlistRepository = wishlistRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
	}

	public WishlistListResponse getWishlists(Long userId, int page, int size) {
		validatePage(page, size);
		Page<Wishlist> wishlistPage = wishlistRepository.findOnSaleWishlistsByUserId(
			userId,
			ProductStatus.ON_SALE,
			PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
		);

		Map<Long, String> thumbnailUrls = getThumbnailUrls(wishlistPage.getContent());
		List<WishlistProductResponse> products = wishlistPage.getContent().stream()
			.map(wishlist -> WishlistProductResponse.from(
				wishlist.getProduct(),
				thumbnailUrls.get(wishlist.getProduct().getId())
			))
			.toList();
		return WishlistListResponse.from(wishlistPage, products);
	}

	@Transactional
	public WishlistStatusResponse addWishlist(Long userId, Long productId) {
		Product product = productRepository.findPublicProductById(productId, ProductStatus.ON_SALE)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

		if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
			throw new BusinessException(ErrorCode.WISHLIST_ALREADY_EXISTS);
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		wishlistRepository.save(Wishlist.create(user, product));
		return WishlistStatusResponse.wished(productId);
	}

	@Transactional
	public WishlistStatusResponse removeWishlist(Long userId, Long productId) {
		Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
			.orElseThrow(() -> new BusinessException(ErrorCode.WISHLIST_NOT_FOUND));

		wishlistRepository.delete(wishlist);
		return WishlistStatusResponse.unwished(productId);
	}

	private void validatePage(int page, int size) {
		if (page < 0 || size < 1 || size > 100) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
	}

	private Map<Long, String> getThumbnailUrls(List<Wishlist> wishlists) {
		List<Long> productIds = wishlists.stream().map(wishlist -> wishlist.getProduct().getId()).toList();
		if (productIds.isEmpty()) {
			return Map.of();
		}

		return productImageRepository.findThumbnailsByProductIds(productIds, ProductImageType.THUMBNAIL)
			.stream()
			.collect(Collectors.toMap(
				ProductImage::getProductId,
				ProductImage::getImageUrl,
				(first, ignored) -> first
			));
	}
}
