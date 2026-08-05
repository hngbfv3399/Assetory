package com.portfolio.assetory.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	Optional<Product> findByName(String name);

	Optional<Product> findByIdAndDeletedAtIsNull(Long productId);

	Page<Product> findBySellerIdAndDeletedAtIsNull(Long sellerId, Pageable pageable);

	Page<Product> findBySellerIdAndStatusAndDeletedAtIsNull(Long sellerId, ProductStatus status, Pageable pageable);

	@Query(
		value = """
			select product from Product product
			where product.deletedAt is null
			  and (:status is null or product.status = :status)
			  and (product.seller.id = :userId or exists (
				select collaborator.id from ProductCollaborator collaborator
				where collaborator.product = product
				  and collaborator.user.id = :userId
				  and collaborator.status = com.portfolio.assetory.collaborator.domain.ProductCollaboratorStatus.ACCEPTED
			  ))
			""",
		countQuery = """
			select count(product) from Product product
			where product.deletedAt is null
			  and (:status is null or product.status = :status)
			  and (product.seller.id = :userId or exists (
				select collaborator.id from ProductCollaborator collaborator
				where collaborator.product = product
				  and collaborator.user.id = :userId
				  and collaborator.status = com.portfolio.assetory.collaborator.domain.ProductCollaboratorStatus.ACCEPTED
			  ))
			"""
	)
	Page<Product> findManageableProducts(
		@Param("userId") Long userId,
		@Param("status") ProductStatus status,
		Pageable pageable
	);

	long countBySellerIdAndStatusAndDeletedAtIsNull(Long sellerId, ProductStatus status);

	@Query(
		value = """
			select product
			from Product product
			left join Review review on review.product = product and review.deletedAt is null
			where product.status = :status
			  and product.deletedAt is null
			  and (:categoryId is null or product.category.id = :categoryId or product.category.parent.id = :categoryId)
			  and (:keyword is null
			       or lower(product.name) like lower(concat('%', :keyword, '%'))
			       or lower(product.summary) like lower(concat('%', :keyword, '%')))
			group by product
			order by count(review.id) desc, product.createdAt desc, product.id desc
			""",
		countQuery = """
			select count(product)
			from Product product
			where product.status = :status
			  and product.deletedAt is null
			  and (:categoryId is null or product.category.id = :categoryId or product.category.parent.id = :categoryId)
			  and (:keyword is null
			       or lower(product.name) like lower(concat('%', :keyword, '%'))
			       or lower(product.summary) like lower(concat('%', :keyword, '%')))
			"""
	)
	Page<Product> findPublicProductsOrderByReviewCount(
		@Param("categoryId") Long categoryId,
		@Param("keyword") String keyword,
		@Param("status") ProductStatus status,
		Pageable pageable
	);

	@Query("""
		select product
		from Product product
		join fetch product.category
		join fetch product.seller
		where product.id = :productId
		  and product.status = :status
		  and product.deletedAt is null
		""")
	Optional<Product> findPublicProductById(
		@Param("productId") Long productId,
		@Param("status") ProductStatus status
	);
}
