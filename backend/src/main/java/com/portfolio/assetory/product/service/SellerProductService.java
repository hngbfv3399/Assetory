package com.portfolio.assetory.product.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.category.domain.Category;
import com.portfolio.assetory.category.repository.CategoryRepository;
import com.portfolio.assetory.collaborator.service.ProductPermissionService;
import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.member.domain.User;
import com.portfolio.assetory.member.repository.UserRepository;
import com.portfolio.assetory.product.domain.Product;
import com.portfolio.assetory.product.domain.ProductImage;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.domain.ProductResource;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.dto.request.CreateSellerProductRequest;
import com.portfolio.assetory.product.dto.request.UpdateSellerProductRequest;
import com.portfolio.assetory.product.dto.request.CreateProductImageRequest;
import com.portfolio.assetory.product.dto.request.CreateProductResourceRequest;
import com.portfolio.assetory.product.dto.request.UpdateProductResourceRequest;
import com.portfolio.assetory.product.dto.response.SellerProductCreateResponse;
import com.portfolio.assetory.product.dto.response.SellerProductDetailResponse;
import com.portfolio.assetory.product.dto.response.SellerProductListResponse;
import com.portfolio.assetory.product.dto.response.SellerProductSummaryResponse;
import com.portfolio.assetory.product.dto.response.SellerProductUpdateResponse;
import com.portfolio.assetory.product.dto.response.SellerProductImageResponse;
import com.portfolio.assetory.product.dto.response.SellerProductResourceResponse;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.product.repository.ProductResourceRepository;
import com.portfolio.assetory.product.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class SellerProductService {
	private static final int MAX_PAGE_SIZE = 100;

	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final ProductResourceRepository productResourceRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final ProductPermissionService productPermissionService;

	public SellerProductService(
		ProductRepository productRepository,
		ProductImageRepository productImageRepository,
		ProductResourceRepository productResourceRepository,
		CategoryRepository categoryRepository,
		UserRepository userRepository,
		ProductPermissionService productPermissionService
	) {
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
		this.productResourceRepository = productResourceRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.productPermissionService = productPermissionService;
	}

	public SellerProductDetailResponse getMyProduct(Long sellerId, Long productId) {
		Product product = productPermissionService.getManageableProduct(sellerId, productId);

		return SellerProductDetailResponse.from(
			product,
			productImageRepository.findByProductIdOrderBySortOrderAscIdAsc(productId),
			productResourceRepository.findByProductIdAndActiveTrueOrderBySortOrderAscIdAsc(productId)
		);
	}

	public SellerProductListResponse getMyProducts(Long sellerId, ProductStatus status, int page, int size) {
		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}

		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
		Page<Product> productPage = productRepository.findManageableProducts(sellerId, status, pageable);
		Map<Long, String> thumbnailUrls = getThumbnailUrls(productPage.getContent());
		List<SellerProductSummaryResponse> products = productPage.getContent().stream()
			.map(product -> SellerProductSummaryResponse.from(product, thumbnailUrls.get(product.getId())))
			.toList();
		return SellerProductListResponse.from(productPage, products);
	}

	@Transactional
	public SellerProductCreateResponse createProduct(Long sellerId, CreateSellerProductRequest request) {
		User seller = userRepository.findById(sellerId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		Category category = categoryRepository.findByIdAndActiveTrue(request.categoryId())
			.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

		Product product = Product.create(
			seller,
			category,
			request.name().trim(),
			request.summary().trim(),
			request.description().trim(),
			null,
			request.price()
		);
		return SellerProductCreateResponse.from(productRepository.save(product));
	}

	@Transactional
	public SellerProductUpdateResponse updateProduct(Long sellerId, Long productId, UpdateSellerProductRequest request) {
		Product product = getOwnedProduct(sellerId, productId);
		Category category = request.categoryId() == null ? null : categoryRepository.findByIdAndActiveTrue(request.categoryId())
			.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
		validateOptionalText(request.name());
		validateOptionalText(request.summary());
		validateOptionalText(request.description());
		product.update(category, trim(request.name()), trim(request.summary()), trim(request.description()), request.price());
		return SellerProductUpdateResponse.from(product);
	}

	@Transactional
	public void deleteProduct(Long sellerId, Long productId) {
		productPermissionService.getOwnedProduct(sellerId, productId).delete();
	}

	@Transactional
	public SellerProductImageResponse addImage(Long sellerId, Long productId, CreateProductImageRequest request) {
		Product product = getOwnedProduct(sellerId, productId);
		if (request.isThumbnail()) productImageRepository.findByProductIdOrderBySortOrderAscIdAsc(productId).forEach(ProductImage::makeDetail);
		ProductImage image = ProductImage.attach(product, request.imageUrl().trim(), request.originalName(), request.isThumbnail() ? ProductImageType.THUMBNAIL : ProductImageType.DETAIL, productImageRepository.findByProductIdOrderBySortOrderAscIdAsc(productId).size() + 1);
		return SellerProductImageResponse.from(productImageRepository.save(image));
	}

	@Transactional
	public SellerProductResourceResponse addResource(Long sellerId, Long productId, CreateProductResourceRequest request) {
		Product product = getOwnedProduct(sellerId, productId);
		ProductResource resource = ProductResource.attach(product, request.type(), request.url().trim(), request.originalName(), request.name().trim(), request.fileSize(), (int) productResourceRepository.countByProductId(productId) + 1);
		return SellerProductResourceResponse.from(productResourceRepository.save(resource));
	}

	@Transactional
	public void deleteImage(Long sellerId, Long productId, Long imageId) {
		getOwnedProduct(sellerId, productId);
		ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAscIdAsc(productId);
		if (image.getImageType() == ProductImageType.THUMBNAIL) {
			ProductImage replacement = images.stream().filter(candidate -> !candidate.getId().equals(imageId)).findFirst().orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT));
			replacement.makeThumbnail();
		}
		productImageRepository.delete(image);
	}

	@Transactional
	public SellerProductResourceResponse updateResource(Long sellerId, Long productId, Long resourceId, UpdateProductResourceRequest request) {
		getOwnedProduct(sellerId, productId);
		validateOptionalText(request.name()); validateOptionalText(request.url());
		if (request.name() == null && request.url() == null) throw new BusinessException(ErrorCode.INVALID_INPUT);
		ProductResource resource = productResourceRepository.findByIdAndProductId(resourceId, productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		resource.update(trim(request.name()), trim(request.url()));
		return SellerProductResourceResponse.from(resource);
	}

	@Transactional
	public void deleteResource(Long sellerId, Long productId, Long resourceId) {
		getOwnedProduct(sellerId, productId);
		ProductResource resource = productResourceRepository.findByIdAndProductId(resourceId, productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
		productResourceRepository.delete(resource);
	}

	@Transactional
	public ProductStatus publishProduct(Long sellerId, Long productId) {
		Product product = getOwnedProduct(sellerId, productId);
		if (product.getStatus() == ProductStatus.ON_SALE) throw new BusinessException(ErrorCode.INVALID_PRODUCT_STATUS);
		if (!productImageRepository.existsByProductIdAndImageType(productId, ProductImageType.THUMBNAIL) || !productResourceRepository.existsByProductIdAndActiveTrue(productId)) throw new BusinessException(ErrorCode.PRODUCT_NOT_READY);
		product.startSale(); return product.getStatus();
	}

	@Transactional
	public ProductStatus suspendProduct(Long sellerId, Long productId) {
		Product product = getOwnedProduct(sellerId, productId);
		if (product.getStatus() != ProductStatus.ON_SALE) throw new BusinessException(ErrorCode.INVALID_PRODUCT_STATUS);
		product.stopSale(); return product.getStatus();
	}

	private Product getManageableProduct(Long userId, Long productId) {
		return productPermissionService.getManageableProduct(userId, productId);
	}

	private Product getOwnedProduct(Long userId, Long productId) {
		return productPermissionService.getOwnedProduct(userId, productId);
	}

	private void validateOptionalText(String value) {
		if (value != null && value.isBlank()) throw new BusinessException(ErrorCode.INVALID_INPUT);
	}

	private String trim(String value) { return value == null ? null : value.trim(); }

	private Map<Long, String> getThumbnailUrls(List<Product> products) {
		List<Long> productIds = products.stream().map(Product::getId).toList();
		if (productIds.isEmpty()) {
			return Map.of();
		}

		return productImageRepository.findThumbnailsByProductIds(productIds, ProductImageType.THUMBNAIL)
			.stream()
			.collect(Collectors.toMap(ProductImage::getProductId, ProductImage::getImageUrl, (first, ignored) -> first));
	}
}
