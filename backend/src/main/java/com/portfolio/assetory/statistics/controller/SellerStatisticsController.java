package com.portfolio.assetory.statistics.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.assetory.global.auth.CurrentUserId;
import com.portfolio.assetory.global.response.ApiResponse;
import com.portfolio.assetory.statistics.domain.ProductStatisticsSort;
import com.portfolio.assetory.statistics.domain.StatisticsUnit;
import com.portfolio.assetory.statistics.dto.response.ProductStatisticsResponse;
import com.portfolio.assetory.statistics.dto.response.SalesStatisticsResponse;
import com.portfolio.assetory.statistics.dto.response.SettlementEstimateResponse;
import com.portfolio.assetory.statistics.dto.response.StatisticsSummaryResponse;
import com.portfolio.assetory.statistics.service.SellerStatisticsService;

@RestController
@RequestMapping("/api/seller/statistics")
public class SellerStatisticsController {
	private final SellerStatisticsService statisticsService;
	public SellerStatisticsController(SellerStatisticsService statisticsService) { this.statisticsService = statisticsService; }

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<StatisticsSummaryResponse>> summary(@CurrentUserId Long sellerId, @RequestParam(required = false) Long productId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseEntity.ok(ApiResponse.success(statisticsService.summary(sellerId, productId, startDate, endDate)));
	}

	@GetMapping("/sales")
	public ResponseEntity<ApiResponse<SalesStatisticsResponse>> sales(@CurrentUserId Long sellerId, @RequestParam(required = false) Long productId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @RequestParam(defaultValue = "DAY") StatisticsUnit unit) {
		return ResponseEntity.ok(ApiResponse.success(statisticsService.sales(sellerId, productId, startDate, endDate, unit)));
	}

	@GetMapping("/products")
	public ResponseEntity<ApiResponse<ProductStatisticsResponse>> products(@CurrentUserId Long sellerId, @RequestParam(required = false) Long productId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "NET_SALES_DESC") ProductStatisticsSort sort) {
		return ResponseEntity.ok(ApiResponse.success(statisticsService.products(sellerId, productId, startDate, endDate, page, size, sort)));
	}

	@GetMapping("/settlement")
	public ResponseEntity<ApiResponse<SettlementEstimateResponse>> settlement(@CurrentUserId Long sellerId, @RequestParam(required = false) Long productId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return ResponseEntity.ok(ApiResponse.success(statisticsService.settlement(sellerId, productId, startDate, endDate)));
	}
}
