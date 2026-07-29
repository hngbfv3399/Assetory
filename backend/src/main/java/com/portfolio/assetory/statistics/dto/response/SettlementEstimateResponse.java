package com.portfolio.assetory.statistics.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SettlementEstimateResponse(
	Period period,
	BigDecimal netSales,
	BigDecimal platformFeeRate,
	BigDecimal platformFee,
	BigDecimal estimatedSettlementAmount,
	String status
) {
	public record Period(LocalDate startDate, LocalDate endDate) {}
}
