package com.portfolio.assetory.collaborator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.collaborator.domain.ProductCollaboratorStatus;
import com.portfolio.assetory.collaborator.domain.ProductCollaboratorRole;
import com.portfolio.assetory.collaborator.repository.ProductCollaboratorRepository;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductPermissionService {
	private final ProductRepository productRepository;
	private final ProductCollaboratorRepository collaboratorRepository;

	public ProductPermissionService(ProductRepository productRepository, ProductCollaboratorRepository collaboratorRepository) {
		this.productRepository = productRepository;
		this.collaboratorRepository = collaboratorRepository;
	}

	public Product getManageableProduct(Long userId, Long productId) {
		Product product = getProduct(productId);
		if (isOwner(product, userId) || collaboratorRepository.existsByProductIdAndUserIdAndStatus(productId, userId, ProductCollaboratorStatus.ACCEPTED)) {
			return product;
		}
		throw new BusinessException(ErrorCode.FORBIDDEN);
	}

	public Product getProductForContentProposal(Long userId, Long productId) {
		Product product = getProduct(productId);
		if (isOwner(product, userId) || hasAcceptedRole(productId, userId, ProductCollaboratorRole.MANAGER, ProductCollaboratorRole.EDITOR)) {
			return product;
		}
		throw new BusinessException(ErrorCode.FORBIDDEN);
	}

	public Product getProductForSalesProposal(Long userId, Long productId) {
		Product product = getProduct(productId);
		if (isOwner(product, userId) || hasAcceptedRole(productId, userId, ProductCollaboratorRole.MANAGER)) {
			return product;
		}
		throw new BusinessException(ErrorCode.FORBIDDEN);
	}

	public Product getProductForStatistics(Long userId, Long productId) {
		Product product = getProduct(productId);
		if (isOwner(product, userId) || hasAcceptedRole(productId, userId, ProductCollaboratorRole.MANAGER, ProductCollaboratorRole.VIEWER)) {
			return product;
		}
		throw new BusinessException(ErrorCode.FORBIDDEN);
	}

	public Product getProductForManagerOperations(Long userId, Long productId) {
		Product product = getProduct(productId);
		if (isOwner(product, userId) || hasAcceptedRole(productId, userId, ProductCollaboratorRole.MANAGER)) {
			return product;
		}
		throw new BusinessException(ErrorCode.FORBIDDEN);
	}

	public Product getOwnedProduct(Long userId, Long productId) {
		Product product = getProduct(productId);
		if (!isOwner(product, userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
		return product;
	}

	private Product getProduct(Long productId) {
		return productRepository.findByIdAndDeletedAtIsNull(productId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	private boolean isOwner(Product product, Long userId) {
		return product.getSeller().getId().equals(userId);
	}

	private boolean hasAcceptedRole(Long productId, Long userId, ProductCollaboratorRole... roles) {
		return collaboratorRepository.existsByProductIdAndUserIdAndStatusAndRoleIn(
			productId,
			userId,
			ProductCollaboratorStatus.ACCEPTED,
			java.util.List.of(roles)
		);
	}
}
