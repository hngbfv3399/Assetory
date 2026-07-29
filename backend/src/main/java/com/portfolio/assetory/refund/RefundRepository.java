package com.portfolio.assetory.refund;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefundRepository extends JpaRepository<Refund, Long> {
	boolean existsByOrderItemId(Long orderItemId);

	@Query("select r from Refund r join fetch r.orderItem i join fetch i.order o join fetch i.product where r.id=:id and o.buyer.id=:buyer")
	Optional<Refund> findForBuyer(@Param("id") Long id, @Param("buyer") Long buyer);

	@Query("select r from Refund r join fetch r.orderItem i join fetch i.order o join fetch i.product where r.id=:id and o.seller.id=:seller")
	Optional<Refund> findForSeller(@Param("id") Long id, @Param("seller") Long seller);

	@Query(value = "select r from Refund r join fetch r.orderItem i join fetch i.order o join fetch i.product where o.buyer.id=:buyer", countQuery = "select count(r) from Refund r join r.orderItem i join i.order o where o.buyer.id=:buyer")
	org.springframework.data.domain.Page<Refund> findAllForBuyer(@Param("buyer") Long buyer, org.springframework.data.domain.Pageable pageable);

	@Query(value = "select r from Refund r join fetch r.orderItem i join fetch i.order o join fetch i.product where o.seller.id=:seller", countQuery = "select count(r) from Refund r join r.orderItem i join i.order o where o.seller.id=:seller")
	org.springframework.data.domain.Page<Refund> findAllForSeller(@Param("seller") Long seller, org.springframework.data.domain.Pageable pageable);

	@Query(value = "select r from Refund r join fetch r.orderItem i join fetch i.order o join fetch i.product where i.product.id=:productId", countQuery = "select count(r) from Refund r join r.orderItem i where i.product.id=:productId")
	org.springframework.data.domain.Page<Refund> findAllForProduct(@Param("productId") Long productId, org.springframework.data.domain.Pageable pageable);

	@Query("""
		select refund from Refund refund
		join fetch refund.orderItem item
		join fetch item.order orders
		join fetch item.product
		where orders.seller.id = :sellerId
		  and refund.status = com.portfolio.assetory.refund.RefundStatus.COMPLETED
		  and refund.completedAt >= :startAt and refund.completedAt < :endAt
		""")
	List<Refund> findCompletedForSellerBetween(
		@Param("sellerId") Long sellerId,
		@Param("startAt") LocalDateTime startAt,
		@Param("endAt") LocalDateTime endAt
	);
}
