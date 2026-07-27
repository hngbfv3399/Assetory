package com.portfolio.assetory.cart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.cart.domain.Cart;
import com.portfolio.assetory.cart.domain.CartItem;
import com.portfolio.assetory.cart.dto.request.AddCartItemRequest;
import com.portfolio.assetory.cart.dto.response.CartItemAddResponse;
import com.portfolio.assetory.cart.dto.response.CartItemRemoveResponse;
import com.portfolio.assetory.cart.dto.response.CartItemResponse;
import com.portfolio.assetory.cart.dto.response.CartResponse;
import com.portfolio.assetory.cart.repository.CartItemRepository;
import com.portfolio.assetory.cart.repository.CartRepository;
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

@Service
@Transactional(readOnly = true)
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;

	public CartService(
		CartRepository cartRepository,
		CartItemRepository cartItemRepository,
		UserRepository userRepository,
		ProductRepository productRepository,
		ProductImageRepository productImageRepository
	) {
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
	}

	@Transactional
	public CartItemAddResponse addItem(Long userId, AddCartItemRequest request) {
		Product product = productRepository.findPublicProductById(request.productId(), ProductStatus.ON_SALE)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		Cart cart = getOrCreateCart(userId);
		if (cartItemRepository.existsByCartIdAndProductId(cart.getId(), product.getId())) {
			throw new BusinessException(ErrorCode.CART_ITEM_ALREADY_EXISTS);
		}

		cartItemRepository.save(CartItem.create(cart, product));
		return CartItemAddResponse.added(product.getId());
	}

	public CartResponse getCart(Long userId) {
		List<CartItem> items = cartItemRepository.findOnSaleItemsByUserId(userId, ProductStatus.ON_SALE);
		if (items.isEmpty()) {
			return CartResponse.empty();
		}

		Map<Long, String> thumbnailUrls = getThumbnailUrls(items);
		List<CartItemResponse> responses = items.stream()
			.map(item -> CartItemResponse.from(item, thumbnailUrls.get(item.getProduct().getId())))
			.toList();
		BigDecimal totalPrice = responses.stream().map(CartItemResponse::price).reduce(BigDecimal.ZERO, BigDecimal::add);
		return new CartResponse(responses, totalPrice, responses.size());
	}

	@Transactional
	public CartItemRemoveResponse removeItem(Long userId, Long cartItemId) {
		CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
		Long productId = item.getProduct().getId();
		cartItemRepository.delete(item);
		return CartItemRemoveResponse.removed(productId);
	}

	@Transactional
	public void clearCart(Long userId) {
		cartItemRepository.deleteAllByUserId(userId);
	}

	private Cart getOrCreateCart(Long userId) {
		return cartRepository.findByUserId(userId)
			.orElseGet(() -> {
				User user = userRepository.findById(userId)
					.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
				return cartRepository.save(Cart.create(user));
			});
	}

	private Map<Long, String> getThumbnailUrls(List<CartItem> items) {
		List<Long> productIds = items.stream().map(item -> item.getProduct().getId()).toList();
		return productImageRepository.findThumbnailsByProductIds(productIds, ProductImageType.THUMBNAIL)
			.stream()
			.collect(Collectors.toMap(ProductImage::getProductId, ProductImage::getImageUrl, (first, ignored) -> first));
	}
}
