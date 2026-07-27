package com.portfolio.assetory.sellerorder.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.order.domain.OrderItem;
import com.portfolio.assetory.order.domain.OrderStatus;
import com.portfolio.assetory.payment.repository.PaymentRepository;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.sellerorder.dto.response.SellerOrderCountsResponse;
import com.portfolio.assetory.sellerorder.dto.response.SellerOrderDetailResponse;
import com.portfolio.assetory.sellerorder.dto.response.SellerOrderListResponse;
import com.portfolio.assetory.sellerorder.repository.SellerOrderItemRepository;

@Service
@Transactional(readOnly = true)
public class SellerOrderService {
	private final SellerOrderItemRepository orderItemRepository;
	private final ProductImageRepository imageRepository;
	private final PaymentRepository paymentRepository;

	public SellerOrderService(SellerOrderItemRepository orderItemRepository, ProductImageRepository imageRepository, PaymentRepository paymentRepository) {
		this.orderItemRepository = orderItemRepository;
		this.imageRepository = imageRepository;
		this.paymentRepository = paymentRepository;
	}

	public SellerOrderListResponse list(Long sellerId, OrderStatus status, Long productId, LocalDate startDate, LocalDate endDate, int page, int size) {
		validate(page, size, startDate, endDate);
		var orders = orderItemRepository.findForSeller(sellerId, status, productId, atStart(startDate), atNextStart(endDate),
			PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "order.createdAt")));
		List<Long> productIds = orders.getContent().stream().map(item -> item.getProduct().getId()).distinct().toList();
		Map<Long, String> thumbnails = imageRepository.findThumbnailsByProductIds(productIds, ProductImageType.THUMBNAIL).stream()
			.collect(Collectors.toMap(image -> image.getProductId(), image -> image.getImageUrl(), (first, ignored) -> first));
		return SellerOrderListResponse.from(orders, thumbnails);
	}

	public SellerOrderDetailResponse detail(Long sellerId, Long orderItemId) {
		OrderItem item = orderItemRepository.findDetailForSeller(sellerId, orderItemId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
		String thumbnail = imageRepository.findThumbnailsByProductIds(List.of(item.getProduct().getId()), ProductImageType.THUMBNAIL).stream()
			.findFirst().map(image -> image.getImageUrl()).orElse(null);
		return SellerOrderDetailResponse.from(item, thumbnail, paymentRepository.findByOrderId(item.getOrder().getId()).orElse(null));
	}

	public SellerOrderCountsResponse counts(Long sellerId, Long productId, LocalDate startDate, LocalDate endDate) {
		validate(0, 1, startDate, endDate);
		LocalDateTime startAt = atStart(startDate);
		LocalDateTime endAt = atNextStart(endDate);
		long total = orderItemRepository.countForSeller(sellerId, null, productId, startAt, endAt);
		long paid = orderItemRepository.countForSeller(sellerId, OrderStatus.PAID, productId, startAt, endAt);
		return new SellerOrderCountsResponse(total, paid, 0, 0, 0, 0);
	}

	private void validate(int page, int size, LocalDate startDate, LocalDate endDate) {
		if (page < 0 || size < 1 || size > 100 || (startDate != null && endDate != null && startDate.isAfter(endDate))) throw new BusinessException(ErrorCode.INVALID_INPUT);
	}
	private LocalDateTime atStart(LocalDate date) { return date == null ? null : date.atStartOfDay(); }
	private LocalDateTime atNextStart(LocalDate date) { return date == null ? null : date.plusDays(1).atStartOfDay(); }
}
