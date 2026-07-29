package com.portfolio.assetory.statistics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.assetory.global.exception.BusinessException;
import com.portfolio.assetory.global.exception.ErrorCode;
import com.portfolio.assetory.collaborator.service.ProductPermissionService;
import com.portfolio.assetory.order.domain.OrderItem;
import com.portfolio.assetory.order.repository.OrderItemRepository;
import com.portfolio.assetory.product.domain.ProductImageType;
import com.portfolio.assetory.product.domain.ProductStatus;
import com.portfolio.assetory.product.repository.ProductImageRepository;
import com.portfolio.assetory.product.repository.ProductRepository;
import com.portfolio.assetory.refund.Refund;
import com.portfolio.assetory.refund.RefundRepository;
import com.portfolio.assetory.statistics.domain.ProductStatisticsSort;
import com.portfolio.assetory.statistics.domain.StatisticsUnit;
import com.portfolio.assetory.statistics.dto.response.ProductStatisticsResponse;
import com.portfolio.assetory.statistics.dto.response.SalesStatisticsResponse;
import com.portfolio.assetory.statistics.dto.response.SettlementEstimateResponse;
import com.portfolio.assetory.statistics.dto.response.StatisticsSummaryResponse;

@Service
@Transactional(readOnly = true)
public class SellerStatisticsService {
	private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
	private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.10");
	private static final int MAX_PAGE_SIZE = 100;

	private final OrderItemRepository orderItemRepository;
	private final RefundRepository refundRepository;
	private final ProductRepository productRepository;
	private final ProductImageRepository imageRepository;
	private final ProductPermissionService permissionService;

	public SellerStatisticsService(
		OrderItemRepository orderItemRepository,
		RefundRepository refundRepository,
		ProductRepository productRepository,
		ProductImageRepository imageRepository, ProductPermissionService permissionService
	) {
		this.orderItemRepository = orderItemRepository;
		this.refundRepository = refundRepository;
		this.productRepository = productRepository;
		this.imageRepository = imageRepository;
		this.permissionService = permissionService;
	}

	public StatisticsSummaryResponse summary(Long userId, Long productId, LocalDate startDate, LocalDate endDate) {
		Scope scope = scope(userId, productId);
		Period period = resolveOptionalPeriod(startDate, endDate);
		Metrics current = metrics(scope, period);
		long days = ChronoUnit.DAYS.between(period.startDate(), period.endDate().plusDays(1));
		Period previous = new Period(period.startDate().minusDays(days), period.startDate().minusDays(1));
		Metrics previousMetrics = metrics(scope, previous);
		BigDecimal changeRate = previousMetrics.netSales().signum() == 0 ? null
			: current.netSales().subtract(previousMetrics.netSales()).multiply(BigDecimal.valueOf(100))
				.divide(previousMetrics.netSales(), 2, RoundingMode.HALF_UP);
		return new StatisticsSummaryResponse(
			new StatisticsSummaryResponse.Period(period.startDate(), period.endDate()),
			current.totalSales(), current.salesCount(), current.customerCount(), current.refundAmount(), current.refundCount(), current.netSales(),
			scope.productId() == null ? productRepository.countBySellerIdAndStatusAndDeletedAtIsNull(scope.sellerId(), ProductStatus.ON_SALE)
				: (scope.product().getStatus() == ProductStatus.ON_SALE ? 1 : 0),
			new StatisticsSummaryResponse.Comparison(previousMetrics.netSales(), changeRate)
		);
	}

	public SalesStatisticsResponse sales(Long userId, Long productId, LocalDate startDate, LocalDate endDate, StatisticsUnit unit) {
		Scope scope = scope(userId, productId);
		Period period = requirePeriod(startDate, endDate);
		Map<LocalDate, MetricsAccumulator> buckets = new HashMap<>();
		for (OrderItem item : completedItems(scope, period)) {
			buckets.computeIfAbsent(bucket(item.getOrder().getCompletedAt().toLocalDate(), unit), ignored -> new MetricsAccumulator()).addSale(item);
		}
		for (Refund refund : completedRefunds(scope, period)) {
			buckets.computeIfAbsent(bucket(refund.getCompletedAt().toLocalDate(), unit), ignored -> new MetricsAccumulator()).addRefund(refund);
		}
		List<SalesStatisticsResponse.Point> points = new ArrayList<>();
		for (LocalDate date = bucket(period.startDate(), unit); !date.isAfter(period.endDate()); date = nextBucket(date, unit)) {
			Metrics metrics = buckets.getOrDefault(date, new MetricsAccumulator()).toMetrics();
			points.add(new SalesStatisticsResponse.Point(date, metrics.totalSales(), metrics.refundAmount(), metrics.netSales(), metrics.salesCount()));
		}
		BigDecimal totalNetSales = points.stream().map(SalesStatisticsResponse.Point::netSales).reduce(ZERO, BigDecimal::add);
		return new SalesStatisticsResponse(new SalesStatisticsResponse.Period(period.startDate(), period.endDate(), unit), totalNetSales, points);
	}

	public ProductStatisticsResponse products(Long userId, Long productId, LocalDate startDate, LocalDate endDate, int page, int size, ProductStatisticsSort sort) {
		Scope scope = scope(userId, productId);
		if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) throw new BusinessException(ErrorCode.INVALID_INPUT);
		Period period = resolveOptionalPeriod(startDate, endDate);
		Map<Long, ProductMetrics> metrics = new HashMap<>();
		for (OrderItem item : completedItems(scope, period)) metrics.computeIfAbsent(item.getProduct().getId(), ignored -> new ProductMetrics(item)).addSale(item);
		for (Refund refund : completedRefunds(scope, period)) metrics.computeIfAbsent(refund.getOrderItem().getProduct().getId(), ignored -> new ProductMetrics(refund.getOrderItem())).addRefund(refund);
		List<ProductMetrics> content = new ArrayList<>(metrics.values());
		content.sort(comparator(sort));
		int from = Math.min(page * size, content.size());
		int to = Math.min(from + size, content.size());
		List<Long> ids = content.subList(from, to).stream().map(ProductMetrics::productId).toList();
		Map<Long, String> thumbnails = imageRepository.findThumbnailsByProductIds(ids, ProductImageType.THUMBNAIL).stream()
			.collect(java.util.stream.Collectors.toMap(image -> image.getProductId(), image -> image.getImageUrl(), (first, ignored) -> first));
		List<ProductStatisticsResponse.Product> responses = content.subList(from, to).stream().map(metric -> metric.toResponse(thumbnails.get(metric.productId()))).toList();
		return new ProductStatisticsResponse(responses, page, size, content.size(), (int) Math.ceil((double) content.size() / size));
	}

	public SettlementEstimateResponse settlement(Long userId, Long productId, LocalDate startDate, LocalDate endDate) {
		Scope scope = scope(userId, productId);
		Period period = resolveOptionalPeriod(startDate, endDate);
		Metrics metrics = metrics(scope, period);
		BigDecimal fee = metrics.netSales().multiply(PLATFORM_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
		return new SettlementEstimateResponse(new SettlementEstimateResponse.Period(period.startDate(), period.endDate()), metrics.netSales(), PLATFORM_FEE_RATE.multiply(BigDecimal.valueOf(100)), fee, metrics.netSales().subtract(fee), "ESTIMATED");
	}

	private Metrics metrics(Scope scope, Period period) {
		MetricsAccumulator accumulator = new MetricsAccumulator();
		completedItems(scope, period).forEach(accumulator::addSale);
		completedRefunds(scope, period).forEach(accumulator::addRefund);
		return accumulator.toMetrics();
	}

	private List<OrderItem> completedItems(Scope scope, Period period) { return orderItemRepository.findCompletedForSellerBetween(scope.sellerId(), period.startAt(), period.endAt()).stream().filter(item -> scope.productId() == null || item.getProduct().getId().equals(scope.productId())).toList(); }
	private List<Refund> completedRefunds(Scope scope, Period period) { return refundRepository.findCompletedForSellerBetween(scope.sellerId(), period.startAt(), period.endAt()).stream().filter(refund -> scope.productId() == null || refund.getOrderItem().getProduct().getId().equals(scope.productId())).toList(); }
	private Scope scope(Long userId, Long productId) {
		if (productId == null) return new Scope(userId, null, null);
		var product = permissionService.getProductForStatistics(userId, productId);
		return new Scope(product.getSeller().getId(), productId, product);
	}
	private Period resolveOptionalPeriod(LocalDate startDate, LocalDate endDate) {
		if (startDate == null && endDate == null) { LocalDate today = LocalDate.now(); return new Period(today.withDayOfMonth(1), today); }
		if (startDate == null || endDate == null) throw new BusinessException(ErrorCode.INVALID_INPUT);
		return requirePeriod(startDate, endDate);
	}
	private Period requirePeriod(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null || startDate.isAfter(endDate)) throw new BusinessException(ErrorCode.INVALID_INPUT);
		return new Period(startDate, endDate);
	}
	private LocalDate bucket(LocalDate date, StatisticsUnit unit) { return switch (unit) { case DAY -> date; case WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); case MONTH -> date.withDayOfMonth(1); }; }
	private LocalDate nextBucket(LocalDate date, StatisticsUnit unit) { return switch (unit) { case DAY -> date.plusDays(1); case WEEK -> date.plusWeeks(1); case MONTH -> date.plusMonths(1); }; }
	private Comparator<ProductMetrics> comparator(ProductStatisticsSort sort) {
		return switch (sort) { case SALES_COUNT_DESC -> Comparator.comparingLong(ProductMetrics::salesCount).reversed().thenComparing(ProductMetrics::productId); case REFUND_RATE_DESC -> Comparator.comparing(ProductMetrics::refundRate).reversed().thenComparing(ProductMetrics::productId); case NET_SALES_DESC -> Comparator.comparing(ProductMetrics::netSales).reversed().thenComparing(ProductMetrics::productId); };
	}

	private record Period(LocalDate startDate, LocalDate endDate) { LocalDateTime startAt() { return startDate.atStartOfDay(); } LocalDateTime endAt() { return endDate.plusDays(1).atStartOfDay(); } }
	private record Scope(Long sellerId, Long productId, com.portfolio.assetory.product.domain.Product product) {}
	private record Metrics(BigDecimal totalSales, long salesCount, long customerCount, BigDecimal refundAmount, long refundCount) { BigDecimal netSales() { return totalSales.subtract(refundAmount); } }
	private static class MetricsAccumulator {
		private BigDecimal totalSales = ZERO; private long salesCount; private final Set<Long> customers = new HashSet<>(); private BigDecimal refundAmount = ZERO; private long refundCount;
		void addSale(OrderItem item) { totalSales = totalSales.add(item.getSubtotalAmount()); salesCount += item.getQuantity(); customers.add(item.getOrder().getBuyer().getId()); }
		void addRefund(Refund refund) { refundAmount = refundAmount.add(refund.getRefundAmount()); refundCount++; }
		Metrics toMetrics() { return new Metrics(totalSales, salesCount, customers.size(), refundAmount, refundCount); }
	}
	private static class ProductMetrics {
		private final OrderItem representative; private BigDecimal totalSales = ZERO; private long salesCount; private BigDecimal refundAmount = ZERO; private long refundCount;
		ProductMetrics(OrderItem item) { representative = item; }
		void addSale(OrderItem item) { totalSales = totalSales.add(item.getSubtotalAmount()); salesCount += item.getQuantity(); }
		void addRefund(Refund refund) { refundAmount = refundAmount.add(refund.getRefundAmount()); refundCount++; }
		Long productId() { return representative.getProduct().getId(); }
		long salesCount() { return salesCount; }
		BigDecimal netSales() { return totalSales.subtract(refundAmount); }
		BigDecimal refundRate() { return salesCount == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(refundCount * 100).divide(BigDecimal.valueOf(salesCount), 2, RoundingMode.HALF_UP); }
		ProductStatisticsResponse.Product toResponse(String thumbnailUrl) { return new ProductStatisticsResponse.Product(productId(), representative.getProductName(), thumbnailUrl, representative.getProduct().getStatus(), salesCount, totalSales, refundCount, refundAmount, netSales(), refundRate()); }
	}
}
