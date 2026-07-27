package com.portfolio.assetory.order.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.portfolio.assetory.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	@Query("select orders from PurchaseOrder orders join fetch orders.buyer where orders.id = :orderId")
	Optional<Order> findWithBuyerById(@Param("orderId") Long orderId);
	Page<Order> findByBuyerIdAndStatus(Long buyerId, com.portfolio.assetory.order.domain.OrderStatus status, Pageable pageable);
	Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
}
