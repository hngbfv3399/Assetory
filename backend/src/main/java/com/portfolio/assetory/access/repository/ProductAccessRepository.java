package com.portfolio.assetory.access.repository;
import org.springframework.data.jpa.repository.JpaRepository; import com.portfolio.assetory.access.domain.ProductAccess;
public interface ProductAccessRepository extends JpaRepository<ProductAccess,Long>{
	@org.springframework.data.jpa.repository.Query("select access from ProductAccess access join fetch access.orderItem item join fetch item.product product where access.user.id=:userId and item.id=:orderItemId and access.status='ACTIVE'")
	java.util.Optional<ProductAccess> findActiveByUserIdAndOrderItemId(@org.springframework.data.repository.query.Param("userId") Long userId,@org.springframework.data.repository.query.Param("orderItemId") Long orderItemId);
	@org.springframework.data.jpa.repository.Query("select access from ProductAccess access join fetch access.orderItem item join fetch item.product product where access.user.id=:userId and product.id=:productId and access.status='ACTIVE'")
	java.util.Optional<ProductAccess> findActiveByUserIdAndProductId(@org.springframework.data.repository.query.Param("userId") Long userId,@org.springframework.data.repository.query.Param("productId") Long productId);
	java.util.Optional<ProductAccess> findByOrderItemId(Long orderItemId);
	@org.springframework.data.jpa.repository.Query(value="select access from ProductAccess access join fetch access.orderItem item join fetch item.product product join fetch product.seller join fetch item.order where access.user.id=:userId and access.status='ACTIVE'",countQuery="select count(access) from ProductAccess access where access.user.id=:userId and access.status='ACTIVE'")
	org.springframework.data.domain.Page<ProductAccess> findActiveByUserId(@org.springframework.data.repository.query.Param("userId") Long userId,org.springframework.data.domain.Pageable pageable);
}
