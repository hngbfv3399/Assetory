package com.portfolio.assetory.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateDirectOrderRequest(@NotNull Long productId) {}
