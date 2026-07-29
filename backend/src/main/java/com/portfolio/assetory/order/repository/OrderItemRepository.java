package com.portfolio.assetory.order.repository;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.portfolio.assetory.order.domain.OrderItem;
import com.portfolio.assetory.order.domain.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	@Query("select item from OrderItem item join fetch item.product where item.order.id = :orderId")
	List<OrderItem> findWithProductByOrderId(@Param("orderId") Long orderId);

	boolean existsByProductIdAndOrderBuyerIdAndOrderStatus(Long productId, Long buyerId, OrderStatus status);

	@Query("""
		select item from OrderItem item
		join fetch item.order orders
		join fetch item.product
		join fetch orders.buyer
		where orders.seller.id = :sellerId
		  and orders.completedAt >= :startAt and orders.completedAt < :endAt
		""")
	List<OrderItem> findCompletedForSellerBetween(
		@Param("sellerId") Long sellerId,
		@Param("startAt") LocalDateTime startAt,
		@Param("endAt") LocalDateTime endAt
	);
}
