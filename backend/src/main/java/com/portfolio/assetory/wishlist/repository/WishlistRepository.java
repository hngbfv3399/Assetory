package com.portfolio.assetory.wishlist.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.wishlist.domain.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

	boolean existsByUserIdAndProductId(Long userId, Long productId);

	Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

	@Query(
		value = """
			select wishlist
			from Wishlist wishlist
			join fetch wishlist.product product
			join fetch product.seller
			where wishlist.user.id = :userId
			  and product.status = :status
			  and product.deletedAt is null
			""",
		countQuery = """
			select count(wishlist)
			from Wishlist wishlist
			where wishlist.user.id = :userId
			  and wishlist.product.status = :status
			  and wishlist.product.deletedAt is null
			"""
	)
	Page<Wishlist> findOnSaleWishlistsByUserId(
		@Param("userId") Long userId,
		@Param("status") ProductStatus status,
		Pageable pageable
	);
}
