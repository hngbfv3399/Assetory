package com.portfolio.assetory.sellerorder.dto.response;

public record SellerOrderCountsResponse(long total, long paid, long refundRequested, long refundApproved,
	long refunded, long refundRejected) {}
