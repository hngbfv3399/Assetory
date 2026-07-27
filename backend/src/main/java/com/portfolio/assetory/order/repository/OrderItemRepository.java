package com.portfolio.assetory.order.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.portfolio.assetory.order.domain.OrderItem;
import com.portfolio.assetory.order.domain.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	@Query("select item from OrderItem item join fetch item.product where item.order.id = :orderId")
	List<OrderItem> findWithProductByOrderId(@Param("orderId") Long orderId);

	boolean existsByProductIdAndOrderBuyerIdAndOrderStatus(Long productId, Long buyerId, OrderStatus status);
}
