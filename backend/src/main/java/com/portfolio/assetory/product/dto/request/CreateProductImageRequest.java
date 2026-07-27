package com.portfolio.assetory.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductImageRequest(@NotBlank String imageUrl, String originalName, @NotNull Boolean isThumbnail) {}
