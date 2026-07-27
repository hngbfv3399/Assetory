package com.portfolio.assetory.purchase.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portfolio.assetory.access.domain.ProductAccess;
import com.portfolio.assetory.access.repository.ProductAccessRepository;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.domain.ProductResource;
import com.portfolio.assetory.product.domain.ProductResourceType;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.product.repository.ProductResourceRepository;
import com.portfolio.assetory.purchase.dto.response.PurchaseDetailResponse;
import com.portfolio.assetory.purchase.dto.response.PurchaseLinkResponse;
import com.portfolio.assetory.purchase.dto.response.PurchaseListResponse;
import com.portfolio.assetory.purchase.dto.response.PurchaseResourceResponse;

@Service
@Transactional(readOnly = true)
public class PurchaseService {
	private final ProductAccessRepository accessRepository;
	private final ProductResourceRepository resourceRepository;
	private final ProductImageRepository imageRepository;
	public PurchaseService(ProductAccessRepository accessRepository, ProductResourceRepository resourceRepository, ProductImageRepository imageRepository) { this.accessRepository = accessRepository; this.resourceRepository = resourceRepository; this.imageRepository = imageRepository; }
	public PurchaseListResponse list(Long userId, int page, int size) {
		if (page < 0 || size < 1 || size > 100) throw new BusinessException(ErrorCode.INVALID_INPUT);
		var accesses = accessRepository.findActiveByUserId(userId, PageRequest.of(page, size));
		List<Long> productIds = accesses.getContent().stream().map(access -> access.getOrderItem().getProduct().getId()).toList();
		Map<Long, String> thumbnails = imageRepository.findThumbnailsByProductIds(productIds, ProductImageType.THUMBNAIL).stream().collect(Collectors.toMap(image -> image.getProductId(), image -> image.getImageUrl(), (first, ignored) -> first));
		return PurchaseListResponse.from(accesses, thumbnails);
	}
	public PurchaseDetailResponse detail(Long userId, Long orderItemId) {
		ProductAccess access = getAccess(userId, orderItemId);
		Long productId = access.getOrderItem().getProduct().getId();
		String thumbnail = imageRepository.findThumbnailsByProductIds(List.of(productId), ProductImageType.THUMBNAIL).stream().findFirst().map(image -> image.getImageUrl()).orElse(null);
		return PurchaseDetailResponse.from(access, thumbnail);
	}
	public PurchaseResourceResponse resources(Long userId, Long orderItemId) { ProductAccess access = getAccess(userId, orderItemId); return PurchaseResourceResponse.from(access.getOrderItem().getProduct().getId(), resourceRepository.findByProductIdAndActiveTrueOrderBySortOrderAscIdAsc(access.getOrderItem().getProduct().getId())); }
	public PurchaseLinkResponse open(Long userId, Long orderItemId, Long resourceId) { ProductAccess access = getAccess(userId, orderItemId); ProductResource resource = resourceRepository.findByIdAndProductId(resourceId, access.getOrderItem().getProduct().getId()).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)); if (resource.getResourceType() != ProductResourceType.LINK) throw new BusinessException(ErrorCode.INVALID_INPUT); return new PurchaseLinkResponse(resource.getId(), resource.getDisplayName(), resource.getResourceUrl()); }
	public String download(Long userId, Long orderItemId, Long resourceId) { ProductAccess access = getAccess(userId, orderItemId); ProductResource resource = resourceRepository.findByIdAndProductId(resourceId, access.getOrderItem().getProduct().getId()).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)); if (resource.getResourceType() != ProductResourceType.FILE) throw new BusinessException(ErrorCode.INVALID_INPUT); return resource.getResourceUrl(); }
	private ProductAccess getAccess(Long userId, Long orderItemId) { return accessRepository.findActiveByUserIdAndOrderItemId(userId, orderItemId).orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN)); }
}
