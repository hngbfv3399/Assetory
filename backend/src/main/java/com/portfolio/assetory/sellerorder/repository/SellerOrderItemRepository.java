package com.portfolio.assetory.sellerorder.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.order.domain.OrderItem;
import com.portfolio.assetory.order.domain.OrderStatus;

public interface SellerOrderItemRepository extends JpaRepository<OrderItem, Long> {
	@Query(value = """
		select item from OrderItem item
		join fetch item.order orders join fetch orders.buyer join fetch item.product
		where orders.seller.id = :sellerId
		and (:status is null or orders.status = :status)
		and (:productId is null or item.product.id = :productId)
		and (:startAt is null or orders.createdAt >= :startAt)
		and (:endAt is null or orders.createdAt < :endAt)
		""", countQuery = """
		select count(item) from OrderItem item join item.order orders
		where orders.seller.id = :sellerId
		and (:status is null or orders.status = :status)
		and (:productId is null or item.product.id = :productId)
		and (:startAt is null or orders.createdAt >= :startAt)
		and (:endAt is null or orders.createdAt < :endAt)
		""")
	Page<OrderItem> findForSeller(@Param("sellerId") Long sellerId, @Param("status") OrderStatus status,
		@Param("productId") Long productId, @Param("startAt") LocalDateTime startAt,
		@Param("endAt") LocalDateTime endAt, Pageable pageable);

	@Query("select item from OrderItem item join fetch item.order orders join fetch orders.buyer join fetch item.product where item.id = :orderItemId and orders.seller.id = :sellerId")
	Optional<OrderItem> findDetailForSeller(@Param("sellerId") Long sellerId, @Param("orderItemId") Long orderItemId);

	@Query("""
		select count(item) from OrderItem item join item.order orders
		where orders.seller.id = :sellerId
		and (:status is null or orders.status = :status)
		and (:productId is null or item.product.id = :productId)
		and (:startAt is null or orders.createdAt >= :startAt)
		and (:endAt is null or orders.createdAt < :endAt)
		""")
	long countForSeller(@Param("sellerId") Long sellerId, @Param("status") OrderStatus status,
		@Param("productId") Long productId, @Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);
}
