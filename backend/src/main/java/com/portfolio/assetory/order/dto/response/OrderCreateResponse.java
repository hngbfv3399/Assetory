package com.portfolio.assetory.order.dto.response;

import java.math.BigDecimal;
import java.util.List;
import com.portfolio.assetory.order.domain.Order;
import com.portfolio.assetory.order.domain.OrderItem;

public record OrderCreateResponse(List<CreatedOrder> orders) {
	public static OrderCreateResponse from(List<Order> orders, java.util.Map<Long, List<OrderItem>> itemsByOrderId) {
		return new OrderCreateResponse(orders.stream().map(order -> CreatedOrder.from(order, itemsByOrderId.get(order.getId()))).toList());
	}
	public record CreatedOrder(Long orderId, String orderNumber, Long sellerId, List<Item> items, BigDecimal totalPrice, String status) {
		static CreatedOrder from(Order order, List<OrderItem> items) {
			return new CreatedOrder(order.getId(), order.getOrderNumber(), order.getSeller().getId(), items.stream().map(Item::from).toList(), order.getTotalAmount(), order.getStatus().name());
		}
	}
	public record Item(Long productId, String name, BigDecimal price) {
		static Item from(OrderItem item) { return new Item(item.getProduct().getId(), item.getProductName(), item.getUnitPrice()); }
	}
}
