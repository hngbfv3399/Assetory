package com.portfolio.assetory.statistics.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.portfolio.assetory.statistics.domain.StatisticsUnit;

public record SalesStatisticsResponse(
	Period period,
	BigDecimal totalNetSales,
	List<Point> points
) {
	public record Period(LocalDate startDate, LocalDate endDate, StatisticsUnit unit) {}
	public record Point(LocalDate date, BigDecimal totalSales, BigDecimal refundAmount, BigDecimal netSales, long salesCount) {}
}
