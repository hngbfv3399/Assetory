package com.portfolio.assetory.statistics.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatisticsSummaryResponse(
	Period period,
	BigDecimal totalSales,
	long salesCount,
	long customerCount,
	BigDecimal refundAmount,
	long refundCount,
	BigDecimal netSales,
	long activeProductCount,
	Comparison comparison
) {
	public record Period(LocalDate startDate, LocalDate endDate) {}
	public record Comparison(BigDecimal previousNetSales, BigDecimal netSalesChangeRate) {}
}
