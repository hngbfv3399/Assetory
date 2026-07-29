package com.portfolio.assetory.collaborator.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.portfolio.assetory.collaborator.domain.ProductChangeRequest;
import com.portfolio.assetory.collaborator.domain.ProductChangeRequestStatus;
import com.portfolio.assetory.collaborator.domain.ProductChangeType;
import com.portfolio.assetory.collaborator.dto.request.CreateProductChangeRequest;
import com.portfolio.assetory.collaborator.dto.request.ReviewProductChangeRequest;
import com.portfolio.assetory.collaborator.dto.response.ProductChangeRequestResponse;
import com.portfolio.assetory.collaborator.repository.ProductChangeRequestRepository;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.repository.UserRepository;
import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.dto.request.CreateProductImageRequest;
import com.portfolio.assetory.product.dto.request.CreateProductResourceRequest;
import com.portfolio.assetory.product.dto.request.UpdateProductResourceRequest;
import com.portfolio.assetory.product.dto.request.UpdateSellerProductRequest;
import com.portfolio.assetory.product.service.SellerProductService;

@Service
@Transactional(readOnly = true)
public class ProductChangeRequestService {
	private final ProductChangeRequestRepository requestRepository;
	private final ProductPermissionService permissionService;
	private final UserRepository userRepository;
	private final SellerProductService sellerProductService;
	private final ObjectMapper objectMapper;

	public ProductChangeRequestService(ProductChangeRequestRepository requestRepository, ProductPermissionService permissionService,
		UserRepository userRepository, SellerProductService sellerProductService, ObjectMapper objectMapper) {
		this.requestRepository = requestRepository;
		this.permissionService = permissionService;
		this.userRepository = userRepository;
		this.sellerProductService = sellerProductService;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public ProductChangeRequestResponse create(Long userId, Long productId, CreateProductChangeRequest input) {
		Product product = productForType(userId, productId, input.type());
		if (product.getSeller().getId().equals(userId)) throw new BusinessException(ErrorCode.INVALID_INPUT);
		User requester = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		String payload = write(input.payload());
		ProductChangeRequest request = requestRepository.save(ProductChangeRequest.create(product, requester, input.type(), payload));
		return ProductChangeRequestResponse.from(request, input.payload());
	}

	public List<ProductChangeRequestResponse> list(Long ownerId, Long productId) {
		permissionService.getOwnedProduct(ownerId, productId);
		return requestRepository.findAllByProductIdWithRequester(productId).stream().map(this::response).toList();
	}

	@Transactional
	public ProductChangeRequestResponse review(Long ownerId, Long requestId, ReviewProductChangeRequest input) {
		if (input.status() != ProductChangeRequestStatus.APPROVED && input.status() != ProductChangeRequestStatus.REJECTED) {
			throw new BusinessException(ErrorCode.INVALID_PRODUCT_CHANGE_REQUEST_STATUS);
		}
		ProductChangeRequest request = requestRepository.findWithDetailsById(requestId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_CHANGE_REQUEST_NOT_FOUND));
		permissionService.getOwnedProduct(ownerId, request.getProduct().getId());
		if (request.getStatus() != ProductChangeRequestStatus.PENDING) {
			throw new BusinessException(ErrorCode.INVALID_PRODUCT_CHANGE_REQUEST_STATUS);
		}
		User owner = userRepository.findById(ownerId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		if (input.status() == ProductChangeRequestStatus.APPROVED) {
			apply(ownerId, request);
			request.approve(owner);
		} else {
			if (input.rejectionReason() == null || input.rejectionReason().isBlank()) throw new BusinessException(ErrorCode.INVALID_INPUT);
			request.reject(owner, input.rejectionReason().trim());
		}
		return response(request);
	}

	private Product productForType(Long userId, Long productId, ProductChangeType type) {
		return switch (type) {
			case PUBLISH, SUSPEND -> permissionService.getProductForSalesProposal(userId, productId);
			default -> permissionService.getProductForContentProposal(userId, productId);
		};
	}

	private void apply(Long ownerId, ProductChangeRequest request) {
		JsonNode payload = read(request.getPayload());
		Long productId = request.getProduct().getId();
		switch (request.getType()) {
			case UPDATE_PRODUCT -> sellerProductService.updateProduct(ownerId, productId, convert(payload, UpdateSellerProductRequest.class));
			case ADD_IMAGE -> sellerProductService.addImage(ownerId, productId, convert(payload, CreateProductImageRequest.class));
			case DELETE_IMAGE -> sellerProductService.deleteImage(ownerId, productId, id(payload, "imageId"));
			case ADD_RESOURCE -> sellerProductService.addResource(ownerId, productId, convert(payload, CreateProductResourceRequest.class));
			case UPDATE_RESOURCE -> sellerProductService.updateResource(ownerId, productId, id(payload, "resourceId"), convert(payload, UpdateProductResourceRequest.class));
			case DELETE_RESOURCE -> sellerProductService.deleteResource(ownerId, productId, id(payload, "resourceId"));
			case PUBLISH -> sellerProductService.publishProduct(ownerId, productId);
			case SUSPEND -> sellerProductService.suspendProduct(ownerId, productId);
		}
	}

	private long id(JsonNode payload, String field) {
		if (!payload.hasNonNull(field) || !payload.get(field).canConvertToLong()) throw new BusinessException(ErrorCode.INVALID_INPUT);
		return payload.get(field).asLong();
	}

	private <T> T convert(JsonNode payload, Class<T> type) {
		try { return objectMapper.treeToValue(payload, type); }
		catch (JacksonException exception) { throw new BusinessException(ErrorCode.INVALID_INPUT); }
	}

	private String write(JsonNode payload) {
		try { return objectMapper.writeValueAsString(payload); }
		catch (JacksonException exception) { throw new BusinessException(ErrorCode.INVALID_INPUT); }
	}

	private JsonNode read(String payload) {
		try { return objectMapper.readTree(payload); }
		catch (JacksonException exception) { throw new BusinessException(ErrorCode.INVALID_INPUT); }
	}

	private ProductChangeRequestResponse response(ProductChangeRequest request) { return ProductChangeRequestResponse.from(request, read(request.getPayload())); }
}
