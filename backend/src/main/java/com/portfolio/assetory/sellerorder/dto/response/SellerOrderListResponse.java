package com.portfolio.assetory.sellerorder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.portfolio.assetory.order.domain.OrderItem;

public record SellerOrderListResponse(List<Order> orders, int page, int size, long totalElements, int totalPages) {

	public static SellerOrderListResponse from(Page<OrderItem> page, java.util.Map<Long, String> thumbnails) {
		return new SellerOrderListResponse(page.getContent().stream().map(item -> new Order(
			item.getId(), item.getOrder().getOrderNumber(),
			new Product(item.getProduct().getId(), item.getProductName(), thumbnails.get(item.getProduct().getId())),
			item.getOrder().getBuyer().getNickname(), item.getUnitPrice(), item.getOrder().getStatus().name(), purchasedAt(item)
		)).toList(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
	}

	private static LocalDateTime purchasedAt(OrderItem item) {
		return item.getOrder().getCompletedAt() != null ? item.getOrder().getCompletedAt() : item.getOrder().getCreatedAt();
	}

	public record Order(Long orderItemId, String orderNumber, Product product, String buyerNickname,
		BigDecimal purchasedPrice, String status, LocalDateTime purchasedAt) {}
	public record Product(Long id, String name, String thumbnailUrl) {}
}
