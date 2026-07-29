package com.portfolio.assetory.collaborator.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.collaborator.domain.ProductCollaborator;
import com.portfolio.assetory.collaborator.domain.ProductCollaboratorStatus;
import com.portfolio.assetory.collaborator.domain.ProductCollaboratorRole;
import com.portfolio.assetory.collaborator.dto.response.ProductCollaboratorResponse;
import com.portfolio.assetory.collaborator.repository.ProductCollaboratorRepository;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.repository.UserRepository;
import com.portfolio.assetory.product.domain.Product;

@Service
@Transactional(readOnly = true)
public class ProductCollaboratorService {
	private final ProductCollaboratorRepository collaboratorRepository;
	private final UserRepository userRepository;
	private final ProductPermissionService productPermissionService;

	public ProductCollaboratorService(
		ProductCollaboratorRepository collaboratorRepository,
		UserRepository userRepository,
		ProductPermissionService productPermissionService
	) {
		this.collaboratorRepository = collaboratorRepository;
		this.userRepository = userRepository;
		this.productPermissionService = productPermissionService;
	}

	@Transactional
	public ProductCollaboratorResponse invite(Long ownerId, Long productId, Long userId, ProductCollaboratorRole role) {
		ProductCollaboratorRole invitationRole = role == null ? ProductCollaboratorRole.EDITOR : role;
		Product product = productPermissionService.getOwnedProduct(ownerId, productId);
		if (ownerId.equals(userId)) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		ProductCollaborator collaborator = collaboratorRepository.findByProductIdAndUserId(productId, userId)
			.map(existing -> reinvite(existing, invitationRole))
			.orElseGet(() -> collaboratorRepository.save(ProductCollaborator.invite(product, user, invitationRole)));
		return ProductCollaboratorResponse.from(collaborator);
	}

	public List<ProductCollaboratorResponse> getCollaborators(Long ownerId, Long productId) {
		productPermissionService.getOwnedProduct(ownerId, productId);
		return collaboratorRepository.findAllByProductIdWithUser(productId).stream()
			.map(ProductCollaboratorResponse::from)
			.toList();
	}

	@Transactional
	public ProductCollaboratorResponse respond(Long userId, Long collaboratorId, ProductCollaboratorStatus status) {
		if (status != ProductCollaboratorStatus.ACCEPTED && status != ProductCollaboratorStatus.REJECTED) {
			throw new BusinessException(ErrorCode.INVALID_COLLABORATOR_STATUS);
		}
		ProductCollaborator collaborator = collaboratorRepository.findById(collaboratorId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_COLLABORATOR_NOT_FOUND));
		if (!collaborator.getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
		if (collaborator.getStatus() != ProductCollaboratorStatus.PENDING) {
			throw new BusinessException(ErrorCode.INVALID_COLLABORATOR_STATUS);
		}
		if (status == ProductCollaboratorStatus.ACCEPTED) collaborator.accept();
		else collaborator.reject();
		return ProductCollaboratorResponse.from(collaborator);
	}

	@Transactional
	public void remove(Long ownerId, Long productId, Long collaboratorId) {
		productPermissionService.getOwnedProduct(ownerId, productId);
		ProductCollaborator collaborator = collaboratorRepository.findById(collaboratorId)
			.filter(candidate -> candidate.getProduct().getId().equals(productId))
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_COLLABORATOR_NOT_FOUND));
		if (collaborator.getStatus() == ProductCollaboratorStatus.REMOVED) {
			throw new BusinessException(ErrorCode.INVALID_COLLABORATOR_STATUS);
		}
		collaborator.remove();
	}

	@Transactional
	public ProductCollaboratorResponse changeRole(Long ownerId, Long productId, Long collaboratorId, ProductCollaboratorRole role) {
		productPermissionService.getOwnedProduct(ownerId, productId);
		ProductCollaborator collaborator = collaboratorRepository.findById(collaboratorId)
			.filter(candidate -> candidate.getProduct().getId().equals(productId))
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_COLLABORATOR_NOT_FOUND));
		if (collaborator.getStatus() == ProductCollaboratorStatus.REMOVED) {
			throw new BusinessException(ErrorCode.INVALID_COLLABORATOR_STATUS);
		}
		collaborator.changeRole(role);
		return ProductCollaboratorResponse.from(collaborator);
	}

	private ProductCollaborator reinvite(ProductCollaborator collaborator, ProductCollaboratorRole role) {
		if (collaborator.getStatus() == ProductCollaboratorStatus.PENDING || collaborator.getStatus() == ProductCollaboratorStatus.ACCEPTED) {
			throw new BusinessException(ErrorCode.PRODUCT_COLLABORATOR_ALREADY_EXISTS);
		}
		collaborator.reinvite(role);
		return collaborator;
	}
}
