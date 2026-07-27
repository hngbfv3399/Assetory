package com.portfolio.assetory.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.cart.domain.CartItem;
import com.portfolio.assetory.product.domain.ProductStatus;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	boolean existsByCartIdAndProductId(Long cartId, Long productId);

	@Query("""
		select item
		from CartItem item
		join fetch item.product product
		join fetch product.seller
		where item.cart.user.id = :userId
		  and product.status = :status
		  and product.deletedAt is null
		order by item.createdAt desc, item.id desc
		""")
	List<CartItem> findOnSaleItemsByUserId(@Param("userId") Long userId, @Param("status") ProductStatus status);

	@Query("""
		select item
		from CartItem item
		join fetch item.product
		where item.id = :cartItemId
		  and item.cart.user.id = :userId
		""")
	Optional<CartItem> findByIdAndUserId(@Param("cartItemId") Long cartItemId, @Param("userId") Long userId);

	@Modifying
	@Query("delete from CartItem item where item.cart.user.id = :userId")
	void deleteAllByUserId(@Param("userId") Long userId);
}
