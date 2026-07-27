package com.portfolio.assetory.order.dto.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateCartOrderRequest(@NotEmpty List<@NotNull Long> productIds) {}
